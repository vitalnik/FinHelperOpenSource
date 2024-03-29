package com.aripuca.finhelper.ui.screens.investment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.R
import com.aripuca.finhelper.calculations.InvestmentCalculator
import com.aripuca.finhelper.calculations.YearlyTableItem
import com.aripuca.finhelper.extensions.checkRange
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.InvestmentHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

enum class Frequency(val value: Int) {
    WEEKLY(52), MONTHLY(12), QUARTERLY(4), ANNUALLY(1);

    companion object {
        fun Int.getFrequency(): Frequency =
            when (this) {
                52 -> WEEKLY
                12 -> MONTHLY
                4 -> QUARTERLY
                else -> ANNUALLY
            }

        @Composable
        fun Frequency.getText(): String =
            when (this) {
                WEEKLY -> stringResource(R.string.weekly_option)
                MONTHLY -> stringResource(R.string.monthly_option)
                QUARTERLY -> stringResource(R.string.quarterly_option)
                ANNUALLY -> stringResource(R.string.annually_option)
            }

        @Composable
        fun Frequency.getText2(): String =
            when (this) {
                WEEKLY -> stringResource(R.string.weekly_option2)
                MONTHLY -> stringResource(R.string.monthly_option2)
                QUARTERLY -> stringResource(R.string.quarterly_option2)
                ANNUALLY -> stringResource(R.string.annually_option2)
            }
    }

}

@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val investmentCalculator: InvestmentCalculator,
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    // history
    val investmentHistoryFlow = appDatabase.investmentHistoryDao().getAllAsFlow()
    val selectedHistoryItemIndex = mutableIntStateOf(localStorage.getInvestmentHistorySelectedIndex(0))
    val saveHistoryItemEnabled = mutableStateOf(false)

    // input fields
    val initialInvestmentInput = mutableStateOf(localStorage.getInvestmentInitialBalance("5000"))
    val interestRateInput = mutableStateOf(localStorage.getInvestmentInterestRate("7"))

    val inputValid = mutableStateOf(false)

    // number of time periods elapsed
    val yearsToGrowInput = mutableStateOf(localStorage.getInvestmentYearsToGrow("10"))
    val regularContributionInput = mutableStateOf(localStorage.getInvestmentRegularAddition("100"))
    val regularAdditionFrequencyInput = mutableIntStateOf(
        localStorage.getInvestmentRegularAdditionFrequency(
            Frequency.MONTHLY.value
        )
    )

    // calculated fields
    val totalInvestment = mutableStateOf("")
    val totalInterestEarned = mutableStateOf("")
    val totalValue = mutableStateOf("")

    val yearlyTable = mutableStateOf<List<YearlyTableItem>>(listOf())
    val principalPercent = mutableDoubleStateOf(50.0)

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_SCREEN_VIEW)
    }

    private fun calculateInvestment() {

        validateRequiredFields()

        if (!inputValid.value) {
            return
        }

        viewModelScope.launch {

            val results = investmentCalculator.calculate(
                initialPrincipalBalance = initialInvestmentInput.value.toDoubleOrNull() ?: 0.0,
                regularAddition = regularContributionInput.value.toDoubleOrNull() ?: 0.0,
                regularAdditionFrequency = regularAdditionFrequencyInput.intValue.toDouble(),
                interestRate = interestRateInput.value.toDoubleOrNull() ?: 0.0,
                numberOfTimesInterestApplied = regularAdditionFrequencyInput.intValue.toDouble(),
                yearsToGrow = yearsToGrowInput.value.toIntOrNull() ?: 0,
            )

            totalValue.value = results.totalValue.toCurrency()
            totalInterestEarned.value = results.totalInterestEarned.toCurrency()
            totalInvestment.value = results.totalInvestment.toCurrency()

            yearlyTable.value = results.yearlyTable

            principalPercent.doubleValue = results.principalPercent

            saveUserInputInLocalStorage()
        }
    }

    private fun saveUserInputInLocalStorage() {
        localStorage.saveString(
            LocalStorage.INVESTMENT_INITIAL_BALANCE, initialInvestmentInput.value
        )
        localStorage.saveString(
            LocalStorage.INVESTMENT_REGULAR_ADDITION,
            regularContributionInput.value
        )
        localStorage.saveString(LocalStorage.INVESTMENT_INTEREST_RATE, interestRateInput.value)
        localStorage.saveString(LocalStorage.INVESTMENT_YEARS_TO_GROW, yearsToGrowInput.value)
        localStorage.saveString(
            LocalStorage.INVESTMENT_REGULAR_ADDITION_FREQUENCY,
            regularAdditionFrequencyInput.intValue.toString()
        )
        localStorage.saveInt(
            LocalStorage.INVESTMENT_HISTORY_SELECTED_INDEX,
            selectedHistoryItemIndex.intValue
        )
    }

    private fun validateInitialInvestment(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000_000.0)
    }

    private fun validateRegularContribution(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000.0)
    }

    private fun validateInterestRate(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

    private fun validateYearsToGrow(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

    fun addHistoryItem() {
        viewModelScope.launch {
            val historyEntity = InvestmentHistoryEntity(
                title = "Investment history item",
                initialInvestment = initialInvestmentInput.value,
                interestRate = interestRateInput.value,
                numberOfYears = yearsToGrowInput.value,
                regularContribution = regularContributionInput.value,
                contributionFrequency = regularAdditionFrequencyInput.intValue.toString(),
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )

            appDatabase.investmentHistoryDao().insert(historyEntity)

            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()
            selectedHistoryItemIndex.intValue = historyList.count() - 1

            analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HISTORY_ADDED)
        }
    }

    fun saveHistoryItem() {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()
            if (historyList.isNotEmpty() && selectedHistoryItemIndex.intValue < historyList.count()) {
                val selectedHistoryItem = historyList[selectedHistoryItemIndex.intValue].copy()
                val historyEntity = InvestmentHistoryEntity(
                    id = selectedHistoryItem.id,
                    title = selectedHistoryItem.title,
                    initialInvestment = initialInvestmentInput.value,
                    interestRate = interestRateInput.value,
                    numberOfYears = yearsToGrowInput.value,
                    regularContribution = regularContributionInput.value,
                    contributionFrequency = regularAdditionFrequencyInput.intValue.toString(),
                    createdAt = selectedHistoryItem.createdAt,
                    updatedAt = Date().toIsoString(),
                )
                appDatabase.investmentHistoryDao().update(historyEntity)
            }
            setSaveHistoryItemEnabled()

            analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HISTORY_SAVED)
        }
    }

    fun deleteHistoryItem() {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()

            if (historyList.isNotEmpty() && selectedHistoryItemIndex.intValue < historyList.count()) {

                val selectedHistoryItem = historyList[selectedHistoryItemIndex.intValue]
                appDatabase.investmentHistoryDao().deleteById(selectedHistoryItem.id)

                if (selectedHistoryItemIndex.intValue == historyList.count() - 1) {
                    decrementSelectedHistoryItemIndex(logAnalytics = false)
                }

                analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HISTORY_DELETED)
            }
        }
    }

    fun decrementSelectedHistoryItemIndex(logAnalytics: Boolean = true) {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()
            if (selectedHistoryItemIndex.intValue > 0 && historyList.isNotEmpty()) {
                selectedHistoryItemIndex.intValue--
            } else {
                if (selectedHistoryItemIndex.intValue == 0 && historyList.isNotEmpty()) {
                    selectedHistoryItemIndex.intValue = historyList.count() - 1
                }
            }
            if (logAnalytics) {
                analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HISTORY_SCROLL)
            }
        }
    }

    fun incrementSelectedHistoryItemIndex() {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()
            if (selectedHistoryItemIndex.intValue < historyList.count() - 1) {
                selectedHistoryItemIndex.intValue++
            } else {
                if (selectedHistoryItemIndex.intValue == historyList.count() - 1) {
                    selectedHistoryItemIndex.intValue = 0
                }
            }
            analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HISTORY_SCROLL)
        }
    }

    fun loadLocallySaved() {
        calculateInvestment()
    }

    fun loadHistoryItem(historyList: List<InvestmentHistoryEntity>) {
        if (historyList.isNotEmpty() && selectedHistoryItemIndex.intValue < historyList.count()) {
            val selectedHistoryItem = historyList[selectedHistoryItemIndex.intValue]
            initialInvestmentInput.value = selectedHistoryItem.initialInvestment
            interestRateInput.value = selectedHistoryItem.interestRate
            yearsToGrowInput.value = selectedHistoryItem.numberOfYears
            regularContributionInput.value = selectedHistoryItem.regularContribution
            regularAdditionFrequencyInput.intValue =
                selectedHistoryItem.contributionFrequency.toInt()

            setSaveHistoryItemEnabled()
            calculateInvestment()
        }
    }

    fun updateInitialInvestment(value: String) {
        if (value.isEmpty()) {
            inputValid.value = false
            initialInvestmentInput.value = value
            return
        }
        if (validateInitialInvestment(value)) {
            initialInvestmentInput.value = value
            setSaveHistoryItemEnabled()
            calculateInvestment()
        }
    }

    fun updateInterestRate(value: String) {
        if (value.isEmpty() || (value.toDoubleOrNull() ?: 0.0) == 0.0) {
            inputValid.value = false
            interestRateInput.value = value
            return
        }
        if (validateInterestRate(value)) {
            interestRateInput.value = value
            setSaveHistoryItemEnabled()
            calculateInvestment()
        }
    }

    fun updateRegularContribution(value: String) {
        if (value.isEmpty()) {
            inputValid.value = false
            regularContributionInput.value = value
            return
        }
        if (validateRegularContribution(value)) {
            regularContributionInput.value = value
            setSaveHistoryItemEnabled()
            calculateInvestment()
        }
    }

    fun updateYearsToGrow(value: String) {
        if (value.isEmpty() || (value.toIntOrNull() ?: 0) == 0) {
            inputValid.value = false
            yearsToGrowInput.value = ""
            return
        }
        if (validateYearsToGrow(value)) {
            yearsToGrowInput.value = value
            setSaveHistoryItemEnabled()
            calculateInvestment()
        }
    }

    fun updateRegularAdditionFrequency(frequency: Frequency) {
        regularAdditionFrequencyInput.intValue = frequency.value
        setSaveHistoryItemEnabled()
        calculateInvestment()
    }

    private fun setSaveHistoryItemEnabled() {

        viewModelScope.launch {

            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()

            if (historyList.isNotEmpty()) {
                val selectedHistoryItem = historyList[selectedHistoryItemIndex.intValue]
                saveHistoryItemEnabled.value =
                    selectedHistoryItem.initialInvestment != initialInvestmentInput.value ||
                            selectedHistoryItem.interestRate != interestRateInput.value ||
                            selectedHistoryItem.numberOfYears != yearsToGrowInput.value ||
                            selectedHistoryItem.regularContribution != regularContributionInput.value ||
                            selectedHistoryItem.contributionFrequency != regularAdditionFrequencyInput.intValue.toString()
            }
        }

    }

    private fun validateRequiredFields() {
        inputValid.value =
            !(initialInvestmentInput.value.isEmpty() ||
                    interestRateInput.value.isEmpty() ||
                    (interestRateInput.value.toDoubleOrNull() ?: 0.0) == 0.0 ||
                    regularContributionInput.value.isEmpty()
                    ) && !((initialInvestmentInput.value.toDoubleOrNull() ?: 0.0) == 0.0 &&
                    (regularContributionInput.value.toDoubleOrNull() ?: 0.0) == 0.0)
    }

}