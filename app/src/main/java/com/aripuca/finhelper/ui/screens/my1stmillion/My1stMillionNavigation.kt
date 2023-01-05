package com.aripuca.finhelper.ui.screens.my1stmillion

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

fun NavGraphBuilder.my1stMillionScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
) {

    composable("my1st_million_screen") {

        val viewModel = hiltViewModel<My1stMillionViewModel>()

        val purchasesList by mainViewModel.purchasesFlow.collectAsState()

        val adsRemoved by remember {
            derivedStateOf {
                purchasesList.checkPurchases() || mainViewModel.isTestDevice()
            }
        }

        // input fields
        val principalAmount by viewModel.initialInvestmentInput
        val interestRate by viewModel.interestRateInput
        val regularAddition by viewModel.regularContributionInput
        val regularAdditionFrequency by viewModel.regularAdditionFrequencyInput

        // calculated fields
        val totalInvestment by viewModel.totalInvestment
        val totalValue by viewModel.totalValue
        val totalInterestEarned by viewModel.totalInterestEarned

        val yearsToGrow by viewModel.yearsToGrow

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
            viewModel.calculateInvestment()
        }

        My1stMillionScreen(
            adsRemoved = adsRemoved,
            totalValue = totalValue,
            totalInterestEarned = totalInterestEarned,
            totalInvestment = totalInvestment,
            initialPrincipalAmount = principalAmount,
            yearsToGrow = yearsToGrow,
            onInitialPrincipalAmountChanged = {
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
            regularAdditionFrequency = regularAdditionFrequency.getFrequency(),
            onRegularAdditionFrequencyChanged = {
                viewModel.updateRegularAdditionFrequency(it)
            },
            onOpenInInvestments = {
                viewModel.logOpenInInvestments()
                viewModel.openInInvestments {
                    nav.navigate("investment_screen")
                }
            },
            onBackPress = {
                nav.popBackStack()
            },
        )
    }
}
