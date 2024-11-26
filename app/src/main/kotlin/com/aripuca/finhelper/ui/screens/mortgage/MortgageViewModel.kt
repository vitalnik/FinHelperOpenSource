package com.aripuca.finhelper.ui.screens.mortgage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.calculations.MortgageCalculator
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.MortgageHistoryEntity
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.common.InputFieldsUpdateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class MortgageViewModel @Inject constructor(
    private val mortgageCalculator: MortgageCalculator,
    private val inputFieldsValidator: MortgageInputFieldsValidator,
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val inputFieldsFlow = MutableStateFlow(getInputFieldsFromLocalStorage())
    val calculatedFieldsFlow = MutableStateFlow(MortgageCalculatedFields())
    val historyPanelFlow = MutableStateFlow(
        HistoryPanelState(
            selectedHistoryItemIndex = localStorage.getMortgageHistorySelectedIndex(0)
        )
    )

    init {
        initializeHistoryState()

        observeInputFieldUpdates()
        observeHistoryPanelUpdates()
    }

    private fun initializeHistoryState() {
        viewModelScope.launch {

            val itemCount = appDatabase.mortgageHistoryDao().getCount()

            var selectedItemIndex = localStorage.getMortgageHistorySelectedIndex(0)
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

    private fun observeInputFieldUpdates() {
        viewModelScope.launch {
            inputFieldsFlow.collectLatest {
                calculateMortgage()
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
                if (localStorage.getMortgageHistorySelectedIndex(0) != it.selectedHistoryItemIndex) {
                    saveSelectedIndexToLocalStorage(it.selectedHistoryItemIndex)
                }
            }
        }
    }

    fun logScreenView() {
        analyticsClient.log(AnalyticsClient.MORTGAGE_SCREEN_VIEW)
    }

    fun logPaymentsScheduleClick() {
        analyticsClient.log(AnalyticsClient.MORTGAGE_PAYMENTS_SCHEDULE_CLICK)
    }

    private fun getInputFieldsFromLocalStorage() =
        with(localStorage) {
            MortgageInputFields(
                updateType = InputFieldsUpdateType.INIT,
                principalAmountInput = getMortgagePrincipalAmount("500000"),
                interestRateInput = getMortgageInterestRate("2.5"),
                numberOfYearsInput = getMortgageNumberOfYears("25"),
                paymentsPerYearInput = getMortgagePaymentsPerYear("12")
            )
        }


    private fun calculateMortgage() {

        val results = mortgageCalculator.calculate(
            calculationParams = inputFieldsFlow.value.toCalculationParams(),
        )

        calculatedFieldsFlow.value = with(results) {
            calculatedFieldsFlow.value.copy(
                payment = results.paymentPerPeriod.toCurrency(),
                totalInterest = results.totalInterest.toCurrency(),
                totalPayments = results.totalPayments.toCurrency(),
                principalAmount = (inputFieldsFlow.value.principalAmountInput.toDoubleOrNull()
                    ?: 0.0).toCurrency(),
                principalPercent = results.principalPercent.toFloat(),
                paymentsSchedule = results.paymentsSchedule,
                yearSummary = results.yearSummary
            )
        }
    }

    private fun saveUserInputToLocalStorage() {
        with(inputFieldsFlow.value) {
            localStorage.saveString(
                LocalStorage.MORTGAGE_PRINCIPAL_AMOUNT,
                principalAmountInput
            )
            localStorage.saveString(
                LocalStorage.MORTGAGE_INTEREST_RATE,
                interestRateInput
            )
            localStorage.saveString(
                LocalStorage.MORTGAGE_NUMBER_OF_YEARS,
                numberOfYearsInput
            )
            localStorage.saveString(
                LocalStorage.MORTGAGE_PAYMENTS_PER_YEAR,
                paymentsPerYearInput
            )
        }
    }

    private fun saveSelectedIndexToLocalStorage(index: Int) {
        localStorage.saveInt(
            LocalStorage.MORTGAGE_HISTORY_SELECTED_INDEX,
            index
        )
    }

    private fun createNewHistoryItem(): MortgageHistoryEntity =
        with(inputFieldsFlow.value) {
            MortgageHistoryEntity(
                title = "Mortgage history item",
                principalAmount = principalAmountInput,
                interestRate = interestRateInput,
                numberOfYears = numberOfYearsInput,
                paymentsPerYear = paymentsPerYearInput,
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )
        }

    fun addHistoryItem() {

        viewModelScope.launch {
            val historyItem = createNewHistoryItem()

            appDatabase.mortgageHistoryDao().insert(historyItem)

            val historyCount: Int = appDatabase.mortgageHistoryDao().getCount()

            historyPanelFlow.value = historyPanelFlow.value.copy(
                historyItemCount = historyCount,
                selectedHistoryItemIndex = historyCount - 1, // select just inserted item
                saveHistoryItemEnabled = false
            )

            analyticsClient.log(AnalyticsClient.MORTGAGE_HISTORY_ADDED)
        }
    }

    fun saveHistoryItem() {

        viewModelScope.launch {

            val historyList: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (historyList.isNotEmpty() &&
                historyPanelFlow.value.selectedHistoryItemIndex < historyList.count()
            ) {
                val selectedHistoryEntity =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                val historyEntity = getUpdatedHistoryEntity(selectedHistoryEntity)

                appDatabase.mortgageHistoryDao().update(historyEntity)
            }

            historyPanelFlow.value = historyPanelFlow.value.copy(
                saveHistoryItemEnabled = false
            )

            analyticsClient.log(AnalyticsClient.MORTGAGE_HISTORY_SAVED)
        }

    }

    private fun getUpdatedHistoryEntity(selectedHistoryItem: MortgageHistoryEntity): MortgageHistoryEntity {
        return with(inputFieldsFlow.value) {
            selectedHistoryItem.copy(
                principalAmount = principalAmountInput,
                interestRate = interestRateInput,
                numberOfYears = numberOfYearsInput,
                paymentsPerYear = paymentsPerYearInput,
                updatedAt = Date().toIsoString(),
            )
        }
    }

    fun deleteHistoryItem() {
        viewModelScope.launch {
            val historyList: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (historyList.isNotEmpty() &&
                historyPanelFlow.value.selectedHistoryItemIndex < historyList.count()
            ) {

                val selectedHistoryItem =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                appDatabase.mortgageHistoryDao().deleteById(selectedHistoryItem.id)

                val historyCount: Int = appDatabase.mortgageHistoryDao().getCount()

                historyPanelFlow.value = historyPanelFlow.value.copy(
                    historyItemCount = historyCount
                )

                if (historyPanelFlow.value.selectedHistoryItemIndex == historyCount) {
                    decrementSelectedHistoryItemIndex(logAnalytics = false)
                }

                analyticsClient.log(AnalyticsClient.MORTGAGE_HISTORY_DELETED)
            }
        }
    }

    fun decrementSelectedHistoryItemIndex(logAnalytics: Boolean = true) {

        viewModelScope.launch {

            val historyCount: Int = appDatabase.mortgageHistoryDao().getCount()

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
                analyticsClient.log(AnalyticsClient.MORTGAGE_HISTORY_SCROLL)
            }
        }
    }

    fun incrementSelectedHistoryItemIndex() {
        viewModelScope.launch {

            val historyCount: Int = appDatabase.mortgageHistoryDao().getCount()

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

            analyticsClient.log(AnalyticsClient.MORTGAGE_HISTORY_SCROLL)
        }
    }

    suspend fun loadHistoryItem(selectedHistoryItemIndex: Int) {

        val historyList: List<MortgageHistoryEntity> =
            appDatabase.mortgageHistoryDao().getAll()

        if (historyList.isNotEmpty() && selectedHistoryItemIndex < historyList.count()) {
            val selectedHistoryItem = historyList[selectedHistoryItemIndex]

            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                principalAmountInput = selectedHistoryItem.principalAmount,
                interestRateInput = selectedHistoryItem.interestRate,
                numberOfYearsInput = selectedHistoryItem.numberOfYears,
                paymentsPerYearInput = selectedHistoryItem.paymentsPerYear
            )
        }
    }

    fun updatePrincipalAmount(value: String) {
        if (inputFieldsValidator.validatePrincipalAmount(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                principalAmountInput = value
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

    fun updateNumberOfYears(value: String) {
        if (inputFieldsValidator.validateNumberOfYears(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                numberOfYearsInput = value
            )
        }
    }

    fun updatePaymentsPerYear(value: String) {
        if (inputFieldsValidator.validatePaymentsPerYear(value)) {
            inputFieldsFlow.value = inputFieldsFlow.value.copy(
                updateType = InputFieldsUpdateType.USER_INPUT,
                paymentsPerYearInput = value
            )
        }
    }

    private fun setSaveHistoryItemEnabled() {
        viewModelScope.launch {
            val historyList: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (historyList.isNotEmpty()) {
                val selectedHistoryItem =
                    historyList[historyPanelFlow.value.selectedHistoryItemIndex]

                historyPanelFlow.value = historyPanelFlow.value.copy(
                    saveHistoryItemEnabled = isHistoryItemEdited(selectedHistoryItem)
                )
            }
        }
    }

    private fun isHistoryItemEdited(selectedHistoryItem: MortgageHistoryEntity) =
        with(inputFieldsFlow.value) {
            selectedHistoryItem.principalAmount != principalAmountInput ||
                    selectedHistoryItem.interestRate != interestRateInput ||
                    selectedHistoryItem.numberOfYears != numberOfYearsInput ||
                    selectedHistoryItem.paymentsPerYear != paymentsPerYearInput
        }

}