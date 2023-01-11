package com.aripuca.finhelper.ui.screens.my1stmillion

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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class My1stMillionViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    // input fields
    val initialInvestmentInput = mutableStateOf(localStorage.getGoalInitialBalance("50000"))
    val interestRateInput = mutableStateOf(localStorage.getGoalInterestRate("7"))

    // number of time periods elapsed
    val regularContributionInput = mutableStateOf(localStorage.getGoalRegularAddition("1200"))
    val regularAdditionFrequencyInput = mutableStateOf(
        localStorage.getGoalRegularAdditionFrequency(
            Frequency.WEEKLY.value
        )
    )

    // calculated fields
    val totalInvestment = mutableStateOf("")
    val totalInterestEarned = mutableStateOf("")
    val totalValue = mutableStateOf("")

    val interestRate = mutableStateOf(50.0)
    val yearsToGrow = mutableStateOf("100")

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.MY1ST_MILLION_SCREEN_VIEW)
    }

    fun logOpenInInvestments() {
        analyticsClient.log(FirebaseAnalyticsClient.MY1ST_MILLION_OPEN_IN_INVESTMENTS_CLICK)
    }

    fun calculateInvestment() {

        viewModelScope.launch {

            val calculator = GoalCalculator(
                initialPrincipalBalance = initialInvestmentInput.value.toDoubleOrNull() ?: 0.0,
                regularAddition = regularContributionInput.value.toDoubleOrNull() ?: 0.0,
                regularAdditionFrequency = regularAdditionFrequencyInput.value.toDouble(),
                interestRate = interestRateInput.value.toDoubleOrNull() ?: 0.0,
                numberOfTimesInterestApplied = regularAdditionFrequencyInput.value.toDouble(),
                goalAmount = 1_000_000.0
            )

            calculator.calculate()

            totalValue.value = calculator.totalValue.toCurrency()
            totalInterestEarned.value = calculator.totalInterestEarned.toCurrency()
            totalInvestment.value = calculator.totalInvestment.toCurrency()
            yearsToGrow.value = calculator.yearsToGrow.toString()

            saveData()
        }
    }

    private fun saveData() {
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
            regularAdditionFrequencyInput.value.toString()
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
        regularAdditionFrequencyInput.value = frequency.value
        calculateInvestment()
    }

    fun openInInvestments(callback: () -> Unit = {}) {
        viewModelScope.launch {
            val historyEntity = InvestmentHistoryEntity(
                title = "My 1st Million history item",
                initialInvestment = initialInvestmentInput.value,
                interestRate = interestRateInput.value,
                numberOfYears = yearsToGrow.value,
                regularContribution = regularContributionInput.value,
                contributionFrequency = regularAdditionFrequencyInput.value.toString(),
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )

            appDatabase.investmentHistoryDao().insert(historyEntity)

            localStorage.saveInt(LocalStorage.INVESTMENT_HISTORY_SELECTED_INDEX, appDatabase.investmentHistoryDao().getAll().count() - 1)

            callback()
        }
    }

}