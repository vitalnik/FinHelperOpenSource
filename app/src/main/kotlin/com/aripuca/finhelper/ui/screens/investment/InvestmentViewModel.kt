package com.aripuca.finhelper.ui.screens.investment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.calculations.InvestmentCalculator
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.InvestmentHistoryEntity
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.common.InputFieldsUpdateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val investmentCalculator: InvestmentCalculator,
    private val inputFieldsValidator: InvestmentInputFieldsValidator,
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val inputFieldsFlow = MutableStateFlow(getInputFieldsFromLocalStorage())
    val calculatedFieldsFlow = MutableStateFlow(InvestmentCalculatedFields())
    val historyPanelFlow = MutableStateFlow(
        HistoryPanelState(
            selectedHistoryItemIndex = localStorage.getInvestmentHistorySelectedIndex(0)
        )
    )

    init {
        initializeHistoryState()

        observeInputFieldUpdates()
        observeHistoryPanelUpdates()
    }

    suspend fun loadHistoryItem(selectedHistoryItemIndex: Int) {

        val historyList: List<InvestmentHistoryEntity> =
            appDatabase.investmentHistoryDao().getAll()

        if (historyList.isNotEmpty() && selectedHistoryItemIndex < historyList.count()) {
            val selectedHistoryItem = historyList[selectedHistoryItemIndex]

            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.FROM_DATABASE,
                initialInvestmentInput = selectedHistoryItem.initialInvestment,
                interestRateInput = selectedHistoryItem.interestRate,
                yearsToGrowInput = selectedHistoryItem.numberOfYears,
                regularContributionInput = selectedHistoryItem.regularContribution,
                regularAdditionFrequencyInput = selectedHistoryItem.contributionFrequency.toInt()
            )
        }
    }

    fun logScreenView() {
        analyticsClient.log(AnalyticsClient.INVESTMENT_SCREEN_VIEW)
    }

    fun logYearlyTableClick() {
        analyticsClient.log(AnalyticsClient.INVESTMENT_YEARLY_TABLE_CLICK)
    }

    /**
     * Adding new history item.
     */
    fun addHistoryItem() {
        viewModelScope.launch {
            val historyItem = createNewHistoryItem()

            appDatabase.investmentHistoryDao().insert(historyItem)

            val historyCount: Int = appDatabase.investmentHistoryDao().getCount()

            historyPanelFlow.value = historyPanelFlow.value.copy(
                historyItemCount = historyCount,
                selectedHistoryItemIndex = historyCount - 1, // select just inserted item
                saveHistoryItemEnabled = false
            )

            analyticsClient.log(AnalyticsClient.INVESTMENT_HISTORY_ADDED)
        }
    }

    /**
     * Saving updated history item.
     */
    fun saveHistoryItem() {
        viewModelScope.launch {

            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()

            if (historyList.isNotEmpty() &&
                historyPanelFlow.value.selectedHistoryItemIndex < historyList.count()
            ) {
                val selectedHistoryEntity =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                val historyEntity = getUpdatedHistoryEntity(selectedHistoryEntity)

                appDatabase.investmentHistoryDao().update(historyEntity)
            }

            historyPanelFlow.value = historyPanelFlow.value.copy(
                saveHistoryItemEnabled = false
            )

            analyticsClient.log(AnalyticsClient.INVESTMENT_HISTORY_SAVED)
        }
    }

    fun deleteHistoryItem() {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()

            if (historyList.isNotEmpty() &&
                historyPanelFlow.value.selectedHistoryItemIndex < historyList.count()
            ) {

                val selectedHistoryItem =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                appDatabase.investmentHistoryDao().deleteById(selectedHistoryItem.id)

                val historyCount: Int = appDatabase.investmentHistoryDao().getCount()

                historyPanelFlow.value = historyPanelFlow.value.copy(
                    historyItemCount = historyCount
                )

                if (historyPanelFlow.value.selectedHistoryItemIndex == historyCount) {
                    decrementSelectedHistoryItemIndex(logAnalytics = false)
                }

                analyticsClient.log(AnalyticsClient.INVESTMENT_HISTORY_DELETED)
            }
        }
    }

    /**
     * Decrementing selected history index.
     * Do not log analytics after item deletion.
     */
    fun decrementSelectedHistoryItemIndex(logAnalytics: Boolean = true) {
        viewModelScope.launch {

            val historyCount: Int = appDatabase.investmentHistoryDao().getCount()

            with(historyPanelFlow.value) {
                if (selectedHistoryItemIndex > 0 && historyCount > 0) {
                    historyPanelFlow.value = copy(
                        selectedHistoryItemIndex = selectedHistoryItemIndex - 1,
                        saveHistoryItemEnabled = false
                    )
                } else {
                    if (selectedHistoryItemIndex == 0 && historyCount > 0) {
                        historyPanelFlow.value = copy(
                            selectedHistoryItemIndex = historyCount - 1,
                            saveHistoryItemEnabled = false
                        )
                    }
                }
            }

            if (logAnalytics) {
                analyticsClient.log(AnalyticsClient.INVESTMENT_HISTORY_SCROLL)
            }
        }
    }

    fun incrementSelectedHistoryItemIndex() {
        viewModelScope.launch {

            val historyCount: Int = appDatabase.investmentHistoryDao().getCount()

            with(historyPanelFlow.value) {
                if (selectedHistoryItemIndex < historyCount - 1) {
                    historyPanelFlow.value = copy(
                        selectedHistoryItemIndex = selectedHistoryItemIndex + 1,
                        saveHistoryItemEnabled = false
                    )
                } else {
                    if (selectedHistoryItemIndex == historyCount - 1) {
                        historyPanelFlow.value = copy(
                            selectedHistoryItemIndex = 0,
                            saveHistoryItemEnabled = false
                        )
                    }
                }
            }

            analyticsClient.log(AnalyticsClient.INVESTMENT_HISTORY_SCROLL)
        }
    }

    fun updateInitialInvestment(value: String) {
        if (inputFieldsValidator.validateInitialInvestment(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                initialInvestmentInput = value
            )
        }
    }

    fun updateInterestRate(value: String) {
        if (inputFieldsValidator.validateInterestRate(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                interestRateInput = value
            )
        }
    }

    fun updateRegularContribution(value: String) {
        if (inputFieldsValidator.validateRegularContribution(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                regularContributionInput = value
            )
        }
    }

    fun updateYearsToGrow(value: String) {
        if (inputFieldsValidator.validateYearsToGrow(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                yearsToGrowInput = value
            )
        }
    }

    fun updateRegularAdditionFrequency(frequency: Frequency) {
        inputFieldsFlow.value = inputFieldsFlow.value.copy(
            updateType = InputFieldsUpdateType.USER_INPUT,
            regularAdditionFrequencyInput = frequency.value
        )
    }

    // ---------------------------------------------------------------------------------------------
    // private methods
    // ---------------------------------------------------------------------------------------------

    private fun observeInputFieldUpdates() {
        viewModelScope.launch {
            inputFieldsFlow.collectLatest {
                calculateInvestment()
                if (it.updateType != InputFieldsUpdateType.INIT) {
                    setSaveHistoryItemEnabled()
                    saveUserInputToLocalStorage()
                }
            }
        }
    }

    private fun observeHistoryPanelUpdates() {
        viewModelScope.launch {
            historyPanelFlow.collectLatest {
                if (localStorage.getInvestmentHistorySelectedIndex(0) != it.selectedHistoryItemIndex) {
                    saveSelectedIndexToLocalStorage(it.selectedHistoryItemIndex)
                }
            }
        }
    }

    private fun getInputFieldsFromLocalStorage() =
        with(localStorage) {
            InvestmentInputFields(
                updateType = InputFieldsUpdateType.INIT,
                initialInvestmentInput = getInvestmentInitialBalance("5000"),
                interestRateInput = getInvestmentInterestRate("7"),
                yearsToGrowInput = getInvestmentYearsToGrow("10"),
                regularContributionInput = getInvestmentRegularAddition("100"),
                regularAdditionFrequencyInput = getInvestmentRegularAdditionFrequency(
                    Frequency.MONTHLY.value
                )
            )
        }

    private fun initializeHistoryState() {
        viewModelScope.launch {

            val itemCount = appDatabase.investmentHistoryDao().getCount()

            var selectedItemIndex = localStorage.getInvestmentHistorySelectedIndex(0)
            if (selectedItemIndex >= itemCount) {
                selectedItemIndex = 0
            }

            historyPanelFlow.value = historyPanelFlow.value.copy(
                selectedHistoryItemIndex = selectedItemIndex,
                historyItemCount = itemCount,
                saveHistoryItemEnabled = false,
            )
        }
    }

    private fun calculateInvestment() {

        val results = investmentCalculator.calculate(
            calculationParams = inputFieldsFlow.value.toCalculationParams(),
        )

        calculatedFieldsFlow.value = with(results) {
            calculatedFieldsFlow.value.copy(
                totalValue = totalValue.toCurrency(),
                totalInterestEarned = totalInterestEarned.toCurrency(),
                totalInvestment = totalInvestment.toCurrency(),
                principalPercent = principalPercent,
                yearlyTable = yearlyTable,
            )
        }
    }

    private fun saveUserInputToLocalStorage() {
        with(inputFieldsFlow.value) {
            localStorage.saveString(
                LocalStorage.INVESTMENT_INITIAL_BALANCE,
                initialInvestmentInput
            )
            localStorage.saveString(
                LocalStorage.INVESTMENT_REGULAR_ADDITION,
                regularContributionInput
            )
            localStorage.saveString(
                LocalStorage.INVESTMENT_INTEREST_RATE,
                interestRateInput
            )
            localStorage.saveString(
                LocalStorage.INVESTMENT_YEARS_TO_GROW,
                yearsToGrowInput
            )
            localStorage.saveString(
                LocalStorage.INVESTMENT_REGULAR_ADDITION_FREQUENCY,
                regularAdditionFrequencyInput.toString()
            )
        }
    }

    private fun saveSelectedIndexToLocalStorage(index: Int) {
        localStorage.saveInt(
            LocalStorage.INVESTMENT_HISTORY_SELECTED_INDEX,
            index
        )
    }

    private fun createNewHistoryItem(): InvestmentHistoryEntity =
        with(inputFieldsFlow.value) {
            InvestmentHistoryEntity(
                title = "Investment history item",
                initialInvestment = initialInvestmentInput,
                interestRate = interestRateInput,
                numberOfYears = yearsToGrowInput,
                regularContribution = regularContributionInput,
                contributionFrequency = regularAdditionFrequencyInput.toString(),
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )
        }

    private fun getUpdatedHistoryEntity(selectedHistoryItem: InvestmentHistoryEntity): InvestmentHistoryEntity {
        return with(inputFieldsFlow.value) {
            selectedHistoryItem.copy(
                initialInvestment = initialInvestmentInput,
                interestRate = interestRateInput,
                numberOfYears = yearsToGrowInput,
                regularContribution = regularContributionInput,
                contributionFrequency = regularAdditionFrequencyInput.toString(),
                updatedAt = Date().toIsoString(),
            )
        }
    }

    private fun setSaveHistoryItemEnabled() {
        viewModelScope.launch {
            val historyList: List<InvestmentHistoryEntity> =
                appDatabase.investmentHistoryDao().getAll()

            if (historyList.isNotEmpty()) {
                val selectedHistoryItem =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                historyPanelFlow.value = historyPanelFlow.value.copy(
                    saveHistoryItemEnabled = isHistoryItemEdited(selectedHistoryItem)
                )
            }
        }
    }

    private fun isHistoryItemEdited(selectedHistoryItem: InvestmentHistoryEntity) =
        with(inputFieldsFlow.value) {
            selectedHistoryItem.initialInvestment != initialInvestmentInput ||
                    selectedHistoryItem.interestRate != interestRateInput ||
                    selectedHistoryItem.numberOfYears != yearsToGrowInput ||
                    selectedHistoryItem.regularContribution != regularContributionInput ||
                    selectedHistoryItem.contributionFrequency != regularAdditionFrequencyInput.toString()
        }
}