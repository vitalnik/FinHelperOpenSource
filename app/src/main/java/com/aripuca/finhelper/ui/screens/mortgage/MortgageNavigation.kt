package com.aripuca.finhelper.ui.screens.mortgage

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.services.billing.checkPurchases
import com.google.accompanist.navigation.animation.composable
import androidx.compose.runtime.remember
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
            viewModel.logScreenView()
            viewModel.calculateSchedule()
        }

        LaunchedEffect(key1 = mortgageHistory, key2 = selectedHistoryItemIndex) {
            if (mortgageHistory.isNotEmpty()) {
                viewModel.loadHistoryItem(mortgageHistory)
            }
        }

        MortgageScreen(
            adsRemoved = adsRemoved,
            payment = payment,
            totalInterest = totalInterest,
            totalPayments = totalPayments,
            principalPercent = principalPercent.toFloat(),
            principalAmount = principalAmount,
            principalAmountInput = principalAmountInput,
            onPrincipalAmountChanged = {
                viewModel.updatePrincipalAmount(it)
            },
            interestRate = interestRateInput,
            onInterestRateChanged = {
                viewModel.updateInterestRate(it)
            },
            numberOfYears = numberOfYearsInput,
            onNumberOfYearsChanged = {
                viewModel.updateNumberOfYears(it)
            },
            paymentsPerYear = paymentsPerYearInput,
            onPaymentsPerYearChanged = {
                viewModel.updatePaymentsPerYear(it)
            },
            paymentsSchedule = paymentsSchedule,
            yearSummary = yearSummary,
            onHelpClick = {
                navigateToHelp()
            },
            onBackPress = {
                nav.popBackStack()
            },
            historyPanelState = HistoryPanelState(
                selectedHistoryItemIndex = selectedHistoryItemIndex,
                historyItemCount = mortgageHistory.count(),
                saveHistoryItemEnabled = saveHistoryItemEnabled,
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
