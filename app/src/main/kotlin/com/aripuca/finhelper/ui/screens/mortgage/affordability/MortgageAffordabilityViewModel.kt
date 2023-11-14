package com.aripuca.finhelper.ui.screens.mortgage.affordability

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.calculations.AffordabilityResults
import com.aripuca.finhelper.calculations.MortgageAffordabilityCalculator
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.MortgageHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TabItem(val text: String, var isSelected: Boolean)

fun String.getNumber() = this.substring(0, this.indexOf(" "))

@HiltViewModel
class MortgageAffordabilityViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase,
    private val mortgageAffordabilityCalculator: MortgageAffordabilityCalculator,
) : ViewModel() {

    var monthlyIncome = mutableStateOf(localStorage.getAffordabilityMonthlyIncome("10000"))
    var monthlyDebtPayment = mutableStateOf(localStorage.getAffordabilityMonthlyDebtPayment("500"))
    var downPayment = mutableStateOf(localStorage.getAffordabilityDownPayment("50000"))
    var interestRate = mutableStateOf(localStorage.getAffordabilityInterestRate("5.0"))
    var numberOfYears = mutableStateOf(localStorage.getAffordabilityNumberOfYears("25"))

    private var calculated: Boolean = false

    val yearsList = listOf(10, 15, 20, 25, 30, 35).map { "$it years" }
    var selectedYearIndex =
        mutableIntStateOf(yearsList.indexOfFirst {
            it.getNumber() == numberOfYears.value
        })

    val calculationResult = MutableStateFlow<AffordabilityResults?>(null)

    val canCalculate = mutableStateOf(true)

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.AFFORDABILITY_SCREEN_VIEW)
    }

    fun updateMonthlyIncome(value: String) {
        monthlyIncome.value = value
        canCalculate.value = value != localStorage.getAffordabilityMonthlyIncome("") || !calculated
    }

    fun updateMonthlyDebtPayment(value: String) {
        monthlyDebtPayment.value = value
        canCalculate.value = value != localStorage.getAffordabilityMonthlyDebtPayment("") || !calculated
    }

    fun updateDownPayment(value: String) {
        downPayment.value = value
        canCalculate.value = value != localStorage.getAffordabilityDownPayment("") || !calculated
    }

    fun updateInterestRate(value: String) {
        interestRate.value = value
        canCalculate.value = value != localStorage.getAffordabilityInterestRate("") || !calculated
    }

    fun selectYear(index: Int) {
        selectedYearIndex.intValue = index
        updateNumberOfYears(yearsList[index].getNumber())
    }

    private fun updateNumberOfYears(value: String) {
        numberOfYears.value = value
        canCalculate.value = value != localStorage.getAffordabilityNumberOfYears("") || !calculated
    }

    fun calculate() {

        calculated = true

        analyticsClient.log(FirebaseAnalyticsClient.AFFORDABILITY_CALCULATE_CLICK)

        validateInput()
        saveInputInLocalStorage()

        val results = mortgageAffordabilityCalculator.calculate(
            monthlyIncome = monthlyIncome.value.toDoubleOrNull() ?: 0.0,
            monthlyDebtPayment = monthlyDebtPayment.value.toDoubleOrNull() ?: 0.0,
            downPayment = downPayment.value.toDoubleOrNull() ?: 0.0,
            interestRate = interestRate.value.toDoubleOrNull() ?: 5.0,
            numberOfYears = numberOfYears.value.toIntOrNull() ?: 25
        )

        calculationResult.value = results
        canCalculate.value = false
    }

    private fun validateInput() {
        if (monthlyIncome.value.toDoubleOrNull() == null) {
            monthlyIncome.value = "0"
        }
        if (monthlyDebtPayment.value.toDoubleOrNull() == null) {
            monthlyDebtPayment.value = "0"
        }
        if (interestRate.value.toDoubleOrNull() == null) {
            interestRate.value = "5.0"
        }
        if (downPayment.value.toDoubleOrNull() == null) {
            downPayment.value = "0"
        }
    }

    private fun saveInputInLocalStorage() {
        localStorage.saveString(
            LocalStorage.AFFORDABILITY_MONTHLY_INCOME, monthlyIncome.value
        )
        localStorage.saveString(
            LocalStorage.AFFORDABILITY_MONTHLY_DEBT_PAYMENT, monthlyDebtPayment.value
        )
        localStorage.saveString(
            LocalStorage.AFFORDABILITY_DOWN_PAYMENT, downPayment.value
        )
        localStorage.saveString(
            LocalStorage.AFFORDABILITY_INTEREST_RATE, interestRate.value
        )
        localStorage.saveString(
            LocalStorage.AFFORDABILITY_NUMBER_OF_YEARS, numberOfYears.value
        )
    }

    fun openInMortgage(callback: () -> Unit = {}) {

        analyticsClient.log(FirebaseAnalyticsClient.AFFORDABILITY_OPEN_IN_MORTGAGE_CLICK)

        viewModelScope.launch {
            val historyEntity = MortgageHistoryEntity(
                title = "Mortgage history item",
                principalAmount = (calculationResult.value?.loanAmount ?: "0.0").toString(),
                interestRate = interestRate.value,
                numberOfYears = numberOfYears.value,
                paymentsPerYear = "12",
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )

            val lastHistoryItemIndex = appDatabase.mortgageHistoryDao().getAll().count() - 1

            appDatabase.mortgageHistoryDao().insert(historyEntity)

            localStorage.saveInt(
                LocalStorage.MORTGAGE_HISTORY_SELECTED_INDEX,
                lastHistoryItemIndex + 1
            )

            callback()
        }
    }
}
