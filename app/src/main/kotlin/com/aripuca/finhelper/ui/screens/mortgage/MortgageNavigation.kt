package com.aripuca.finhelper.ui.screens.mortgage

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.common.HistoryPanelEvents
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState

fun NavGraphBuilder.mortgageScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
    navigateToHelp: () -> Unit = {},
) {
    composable("mortgage_screen") {

        val viewModel = hiltViewModel<MortgageViewModel>()

        val purchasesList by mainViewModel.purchasesFlow.collectAsState()

        val adsRemoved by remember {
            derivedStateOf {
                purchasesList.checkPurchases() || mainViewModel.isTestDevice()
            }
        }

        // input fields
        val principalAmountInput by viewModel.principalAmountInput
        val interestRateInput by viewModel.interestRateInput
        val numberOfYearsInput by viewModel.numberOfYearsInput
        val paymentsPerYearInput by viewModel.paymentsPerYearInput

        val inputValid by viewModel.inputValid

        // history
        val selectedHistoryItemIndex by viewModel.selectedHistoryItemIndex
        val saveHistoryItemEnabled by viewModel.saveHistoryItemEnabled
        val mortgageHistory by viewModel.mortgageHistoryFlow.collectAsState(mutableListOf())

        // calculated fields
        val payment by viewModel.paymentState
        val totalInterest by viewModel.totalInterestState
        val totalPayments by viewModel.totalPaymentsState
        val principalAmount by viewModel.principalAmountState

        val paymentsSchedule by viewModel.paymentsSchedule
        val yearSummary by viewModel.yearSummary

        val principalPercent by viewModel.principalPercent

        LaunchedEffect(key1 = true) {
            with(viewModel) {
                logScreenView()
                loadHistorySelectedIndex()
            }
        }

        LaunchedEffect(key1 = mortgageHistory, key2 = selectedHistoryItemIndex) {
            if (mortgageHistory.isNotEmpty()) {
                viewModel.loadHistoryItem(mortgageHistory)
            } else {
                viewModel.loadLocallySaved()
            }
        }

        MortgageScreen(
            screenState = MortgageScreenState(
                adsRemoved = adsRemoved,
                inputValid = inputValid,
                payment = payment,
                totalInterest = totalInterest,
                totalPayments = totalPayments,
                principalPercent = principalPercent.toFloat(),
                principalAmount = principalAmount,
                principalAmountInput = principalAmountInput,
                interestRate = interestRateInput,
                numberOfYears = numberOfYearsInput,
                paymentsPerYear = paymentsPerYearInput,
                paymentsSchedule = paymentsSchedule,
                yearSummary = yearSummary,
            ),
            screenEvents = MortgageScreenEvents(
                onPrincipalAmountChanged = {
                    viewModel.updatePrincipalAmount(it)
                },
                onInterestRateChanged = {
                    viewModel.updateInterestRate(it)
                },
                onNumberOfYearsChanged = {
                    viewModel.updateNumberOfYears(it)
                },
                onPaymentsPerYearChanged = {
                    viewModel.updatePaymentsPerYear(it)
                },
                onHelpClick = {
                    navigateToHelp()
                },
                onAffordabilityClicked = {
                    nav.navigate("mortgage_affordability_screen")
                },
                onBackPress = {
                    nav.popBackStack()
                }
            ),
            historyPanelState = HistoryPanelState(
                selectedHistoryItemIndex = selectedHistoryItemIndex,
                historyItemCount = mortgageHistory.count(),
                saveHistoryItemEnabled = saveHistoryItemEnabled,
            ),
            historyPanelEvents = HistoryPanelEvents(
                onAddHistoryItem = {
                    viewModel.addHistoryItem()
                },
                onSaveHistoryItem = {
                    viewModel.saveHistoryItem()
                },
                onDeleteHistoryItem = {
                    viewModel.deleteHistoryItem()
                },
                onLoadPrevHistoryItem = {
                    viewModel.decrementSelectedHistoryItemIndex()
                },
                onLoadNextHistoryItem = {
                    viewModel.incrementSelectedHistoryItemIndex()
                }
            )
        )
    }
}
