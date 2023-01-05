package com.aripuca.finhelper.ui.screens.mortgage

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aripuca.finhelper.calculations.MortgageCalculator
import com.aripuca.finhelper.calculations.ScheduleItem
import com.aripuca.finhelper.extensions.checkRange
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.extensions.toIsoString
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import com.aripuca.finhelper.services.history.AppDatabase
import com.aripuca.finhelper.services.history.MortgageHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class MortgageViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val analyticsClient: AnalyticsClient,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val mortgageHistoryFlow = appDatabase.mortgageHistoryDao().getAllAsFlow()
    val selectedHistoryItemIndex = mutableStateOf(localStorage.getMortgageHistorySelectedIndex(0))
    val saveHistoryItemEnabled = mutableStateOf(false)

    val paymentState = mutableStateOf("")
    val totalInterestState = mutableStateOf("")
    val totalPaymentsState = mutableStateOf("")
    val principalAmountState = mutableStateOf("")

    val principalAmountInput = mutableStateOf(localStorage.getMortgagePrincipalAmount("500000"))
    val interestRateInput = mutableStateOf(localStorage.getMortgageInterestRate("2.5"))
    val numberOfYearsInput = mutableStateOf(localStorage.getMortgageNumberOfYears("25"))
    val paymentsPerYearInput = mutableStateOf(localStorage.getMortgagePaymentsPerYear("12"))

    val principalPercent = mutableStateOf(50.0)

    val paymentsSchedule = mutableStateOf<List<ScheduleItem>>(listOf())
    val yearSummary = mutableStateOf<List<ScheduleItem>>(listOf())
    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_SCREEN_VIEW)
    }

    private fun validatePrincipalAmount(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000_000.0)
    }

    private fun validateNumberOfYears(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 35.0)
    }

    private fun validatePaymentsPerYear(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 365.0)
    }

    private fun validateInterestRate(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

    fun calculateSchedule() {
        viewModelScope.launch {
            val mortgageCalculator = MortgageCalculator(
                loanAmount = principalAmountInput.value.toDoubleOrNull() ?: 0.0,
                interestRate = interestRateInput.value.toDoubleOrNull() ?: 0.0,
                numberOfYears = numberOfYearsInput.value.toIntOrNull() ?: 0,
                paymentsPerYear = paymentsPerYearInput.value.toIntOrNull() ?: 0,
            )

            mortgageCalculator.calculatePaymentsSchedule()

            val payment = mortgageCalculator.calculatePaymentPerPeriod()
            paymentState.value = payment.toCurrency()

            paymentsSchedule.value = mortgageCalculator.paymentsSchedule
            yearSummary.value = mortgageCalculator.yearSummary
            totalInterestState.value = mortgageCalculator.totalInterest.toCurrency()
            totalPaymentsState.value = mortgageCalculator.totalPayments.toCurrency()
            principalAmountState.value =
                (principalAmountInput.value.toDoubleOrNull() ?: 0.0).toCurrency()
            principalPercent.value =
                ceil(
                    (principalAmountInput.value.toDoubleOrNull()
                        ?: 0.0) * 100.0 / mortgageCalculator.totalPayments
                )
            saveUserInputInLocalStorage()
        }
    }

    private fun saveUserInputInLocalStorage() {
        localStorage.saveString(
            LocalStorage.MORTGAGE_PRINCIPAL_AMOUNT,
            principalAmountInput.value
        )
        localStorage.saveString(LocalStorage.MORTGAGE_INTEREST_RATE, interestRateInput.value)
        localStorage.saveString(
            LocalStorage.MORTGAGE_NUMBER_OF_YEARS,
            numberOfYearsInput.value
        )
        localStorage.saveString(LocalStorage.MORTGAGE_PAYMENTS_PER_YEAR, paymentsPerYearInput.value)
        localStorage.saveInt(LocalStorage.MORTGAGE_HISTORY_SELECTED_INDEX, selectedHistoryItemIndex.value)
    }

    fun addHistoryItem() {
        viewModelScope.launch {
            val mortgageHistoryEntity = MortgageHistoryEntity(
                title = "Mortgage history item",
                principalAmount = principalAmountInput.value,
                interestRate = interestRateInput.value,
                numberOfYears = numberOfYearsInput.value,
                paymentsPerYear = paymentsPerYearInput.value,
                createdAt = Date().toIsoString(),
                updatedAt = Date().toIsoString(),
            )

            appDatabase.mortgageHistoryDao().insert(mortgageHistoryEntity)

            val mortgageHistory: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()
            selectedHistoryItemIndex.value = mortgageHistory.count() - 1

            analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_HISTORY_ADDED)
        }
    }

    fun saveHistoryItem() {
        viewModelScope.launch {
            val mortgageHistory: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (mortgageHistory.isNotEmpty() && selectedHistoryItemIndex.value < mortgageHistory.count()) {

                val selectedHistoryItem = mortgageHistory[selectedHistoryItemIndex.value].copy()

                val mortgageHistoryEntity = MortgageHistoryEntity(
                    id = selectedHistoryItem.id,
                    title = selectedHistoryItem.title,
                    principalAmount = principalAmountInput.value,
                    interestRate = interestRateInput.value,
                    numberOfYears = numberOfYearsInput.value,
                    paymentsPerYear = paymentsPerYearInput.value,
                    createdAt = selectedHistoryItem.createdAt,
                    updatedAt = Date().toIsoString(),
                )
                appDatabase.mortgageHistoryDao().update(mortgageHistoryEntity)
            }
            setSaveHistoryItemEnabled()

            analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_HISTORY_SAVED)
        }
    }

    fun deleteHistoryItem() {
        viewModelScope.launch {
            val historyList: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (historyList.isNotEmpty() && selectedHistoryItemIndex.value < historyList.count()) {

                val selectedHistoryItem = historyList[selectedHistoryItemIndex.value]
                appDatabase.mortgageHistoryDao().deleteById(selectedHistoryItem.id)

                if (selectedHistoryItemIndex.value == historyList.count() - 1) {
                    decrementSelectedHistoryItemIndex(logAnalytics = false)
                }

                analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_HISTORY_DELETED)
            }
        }
    }

    fun decrementSelectedHistoryItemIndex(logAnalytics: Boolean = true) {
        viewModelScope.launch {

            val mortgageHistory: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (selectedHistoryItemIndex.value > 0 && mortgageHistory.isNotEmpty()) {
                selectedHistoryItemIndex.value--
            } else {
                if (selectedHistoryItemIndex.value == 0 && mortgageHistory.isNotEmpty()) {
                    selectedHistoryItemIndex.value = mortgageHistory.count() - 1
                }
            }

            if (logAnalytics) {
                analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_HISTORY_SCROLL)
            }
        }
    }

    fun incrementSelectedHistoryItemIndex() {
        viewModelScope.launch {
            val mortgageHistory: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()
            if (selectedHistoryItemIndex.value < mortgageHistory.count() - 1) {
                selectedHistoryItemIndex.value++
            } else {
                if (selectedHistoryItemIndex.value == mortgageHistory.count() - 1) {
                    selectedHistoryItemIndex.value = 0
                }
            }
            analyticsClient.log(FirebaseAnalyticsClient.MORTGAGE_HISTORY_SCROLL)
        }
    }

    fun loadHistoryItem(mortgageHistory: List<MortgageHistoryEntity>) {
        if (mortgageHistory.isNotEmpty() && selectedHistoryItemIndex.value < mortgageHistory.count()) {
            val selectedHistoryItem = mortgageHistory[selectedHistoryItemIndex.value]
            principalAmountInput.value = selectedHistoryItem.principalAmount
            interestRateInput.value = selectedHistoryItem.interestRate
            numberOfYearsInput.value = selectedHistoryItem.numberOfYears
            paymentsPerYearInput.value = selectedHistoryItem.paymentsPerYear

            setSaveHistoryItemEnabled()
            calculateSchedule()
        }
    }

    fun updatePrincipalAmount(value: String) {
        if (validatePrincipalAmount(value)) {
            principalAmountInput.value = value
            setSaveHistoryItemEnabled()
            calculateSchedule()
        }
    }

    fun updateInterestRate(value: String) {
        if (validateInterestRate(value)) {
            interestRateInput.value = value
            setSaveHistoryItemEnabled()
            calculateSchedule()
        }
    }

    fun updateNumberOfYears(value: String) {
        if (validateNumberOfYears(value)) {
            numberOfYearsInput.value = value
            setSaveHistoryItemEnabled()
            calculateSchedule()
        }
    }

    fun updatePaymentsPerYear(value: String) {
        if (validatePaymentsPerYear(value)) {
            paymentsPerYearInput.value = value
            setSaveHistoryItemEnabled()
            calculateSchedule()
        }
    }

    private fun setSaveHistoryItemEnabled() {
        viewModelScope.launch {

            val mortgageHistory: List<MortgageHistoryEntity> =
                appDatabase.mortgageHistoryDao().getAll()

            if (mortgageHistory.isNotEmpty()) {
                val selectedHistoryItem = mortgageHistory[selectedHistoryItemIndex.value]
                saveHistoryItemEnabled.value =
                    selectedHistoryItem.principalAmount != principalAmountInput.value ||
                            selectedHistoryItem.interestRate != interestRateInput.value ||
                            selectedHistoryItem.numberOfYears != numberOfYearsInput.value ||
                            selectedHistoryItem.paymentsPerYear != paymentsPerYearInput.value
            }
        }

    }
}