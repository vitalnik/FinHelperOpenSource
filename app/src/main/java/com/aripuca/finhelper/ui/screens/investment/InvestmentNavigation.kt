package com.aripuca.finhelper.ui.screens.investment

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getFrequency
import com.google.accompanist.navigation.animation.composable
import androidx.compose.runtime.remember
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState

fun NavGraphBuilder.investmentScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
    navigateToHelp: () -> Unit = {},
) {

    composable("investment_screen") {

        val viewModel = hiltViewModel<InvestmentViewModel>()

        val purchasesList by mainViewModel.purchasesFlow.collectAsState()

        val adsRemoved by remember {
            derivedStateOf {
                purchasesList.checkPurchases() || mainViewModel.isTestDevice()
            }
        }

        // input fields
        val principalAmount by viewModel.initialInvestmentInput
        val interestRate by viewModel.interestRateInput
        val yearsToGrow by viewModel.yearsToGrowInput
        val regularAddition by viewModel.regularContributionInput
        val regularAdditionFrequency by viewModel.regularAdditionFrequencyInput

        // history
        val selectedHistoryItemIndex by viewModel.selectedHistoryItemIndex
        val saveHistoryItemEnabled by viewModel.saveHistoryItemEnabled
        val investmentHistory by viewModel.investmentHistoryFlow.collectAsState(mutableListOf())

        // calculated fields
        val totalInvestment by viewModel.totalInvestment
        val totalValue by viewModel.totalValue
        val totalInterestEarned by viewModel.totalInterestEarned

        val principalPercent by viewModel.principalPercent
        val yearlyTable by viewModel.yearlyTable

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
            viewModel.calculateInvestment()
        }

        val historyPanelState = HistoryPanelState(
            selectedHistoryItemIndex = selectedHistoryItemIndex,
            historyItemCount = investmentHistory.count(),
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

        LaunchedEffect(key1 = investmentHistory, key2 = selectedHistoryItemIndex) {
            if (investmentHistory.isNotEmpty()) {
                viewModel.loadHistoryItem(investmentHistory)
            }
        }

        InvestmentScreen(
            adsRemoved = adsRemoved,
            totalValue = totalValue,
            totalInterestEarned = totalInterestEarned,
            totalInvestment = totalInvestment,
            principalPercent = principalPercent.toFloat(),
            principalAmount = principalAmount,
            onPrincipalAmountChanged = {
                viewModel.updateInitialInvestment(it)
            },
            regularAddition = regularAddition,
            onRegularAdditionChanged = {
                viewModel.updateRegularContribution(it)
            },
            interestRate = interestRate,
            onInterestRateChanged = {
                viewModel.updateInterestRate(it)
            },
            yearsToGrow = yearsToGrow,
            onYearsToGrowChanged = {
                viewModel.updateYearsToGrow(it)
            },
            regularAdditionFrequency = regularAdditionFrequency.getFrequency(),
            onRegularAdditionFrequencyChanged = {
                viewModel.updateRegularAdditionFrequency(it)
            },
            yearlyTable = yearlyTable,
            onHelpClick = {
                navigateToHelp()
            },
            onBackPress = {
                nav.popBackStack()
            },
            historyPanelState = historyPanelState,
        )
    }
}
