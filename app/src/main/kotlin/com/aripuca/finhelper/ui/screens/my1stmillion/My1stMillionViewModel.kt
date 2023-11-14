package com.aripuca.finhelper.ui.screens.my1stmillion

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.calculations.GoalCalculator
import com.aripuca.finhelper.extensions.checkRange
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.InvestmentHistoryEntity
import com.aripuca.finhelper.ui.screens.investment.Frequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class My1stMillionViewModel @Inject constructor(
    private val goalCalculator: GoalCalculator,
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    // input fields
    val initialInvestmentInput = mutableStateOf(localStorage.getGoalInitialBalance("50000"))
    val interestRateInput = mutableStateOf(localStorage.getGoalInterestRate("7"))

    // number of time periods elapsed
    val regularContributionInput = mutableStateOf(localStorage.getGoalRegularAddition("1200"))
    val regularAdditionFrequencyInput = mutableIntStateOf(
        localStorage.getGoalRegularAdditionFrequency(
            Frequency.WEEKLY.value
        )
    )

    // calculated fields
    val totalInvestment = mutableStateOf("")
    val totalInterestEarned = mutableStateOf("")
    private val totalValue = mutableStateOf("")

    val interestRate = mutableDoubleStateOf(50.0)
    val yearsToGrow = mutableStateOf("100")

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.MY1ST_MILLION_SCREEN_VIEW)
    }

    fun calculateInvestment() {

        viewModelScope.launch {

            val result = goalCalculator.calculate(
                initialPrincipalBalance = initialInvestmentInput.value.toDoubleOrNull() ?: 0.0,
                regularAddition = regularContributionInput.value.toDoubleOrNull() ?: 0.0,
                regularAdditionFrequency = regularAdditionFrequencyInput.intValue.toDouble(),
                interestRate = interestRateInput.value.toDoubleOrNull() ?: 0.0,
                numberOfTimesInterestApplied = regularAdditionFrequencyInput.intValue.toDouble(),
                goalAmount = 1_000_000.0
            )


            totalValue.value = result.totalValue.toCurrency()
            totalInterestEarned.value = result.totalInterestEarned.toCurrency()
            totalInvestment.value = result.totalInvestment.toCurrency()
            yearsToGrow.value = result.yearsToGrow.toString()

            saveInputInLocalStorage()
        }
    }

    private fun saveInputInLocalStorage() {
        localStorage.saveString(
            LocalStorage.GOAL_INITIAL_BALANCE, initialInvestmentInput.value
        )
        localStorage.saveString(
            LocalStorage.GOAL_REGULAR_ADDITION,
            regularContributionInput.value
        )
        localStorage.saveString(LocalStorage.GOAL_INTEREST_RATE, interestRateInput.value)
        localStorage.saveString(
            LocalStorage.GOAL_REGULAR_ADDITION_FREQUENCY,
            regularAdditionFrequencyInput.intValue.toString()
        )
    }

    private fun validateInitialInvestment(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000.0)
    }

    private fun validateRegularContribution(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000.0)
    }

    private fun validateInterestRate(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

    fun updateInitialInvestment(value: String) {
        if (validateInitialInvestment(value)) {
            initialInvestmentInput.value = value
            calculateInvestment()
        }
    }

    fun updateInterestRate(value: String) {
        if (validateInterestRate(value)) {
            interestRateInput.value = value
            calculateInvestment()
        }
    }

    fun updateRegularContribution(value: String) {
        if (validateRegularContribution(value)) {
            regularContributionInput.value = value
            calculateInvestment()
        }
    }

    fun updateRegularAdditionFrequency(frequency: Frequency) {
        regularAdditionFrequencyInput.intValue = frequency.value
        calculateInvestment()
    }

    fun openInInvestments(callback: () -> Unit = {}) {

        analyticsClient.log(FirebaseAnalyticsClient.MY1ST_MILLION_OPEN_IN_INVESTMENTS_CLICK)

        viewModelScope.launch {
            val historyEntity = InvestmentHistoryEntity(
                title = "My 1st Million history item",
                initialInvestment = initialInvestmentInput.value,
                interestRate = interestRateInput.value,
                numberOfYears = yearsToGrow.value,
                regularContribution = regularContributionInput.value,
                contributionFrequency = regularAdditionFrequencyInput.intValue.toString(),
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )

            val lastHistoryItemIndex = appDatabase.investmentHistoryDao().getAll().count() - 1

            appDatabase.investmentHistoryDao().insert(historyEntity)

            localStorage.saveInt(
                LocalStorage.INVESTMENT_HISTORY_SELECTED_INDEX,
                lastHistoryItemIndex + 1
            )

            callback()
        }
    }

}