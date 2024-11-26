package com.aripuca.finhelper.ui.screens.my1stmillion

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.InvestmentScreenRoute
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.My1stMillionScreenRoute
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getFrequency

fun NavGraphBuilder.my1stMillionScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
) {

    composable<My1stMillionScreenRoute> {

        val viewModel = hiltViewModel<My1stMillionViewModel>()

        val purchasesList by mainViewModel.purchasesFlow.collectAsStateWithLifecycle()

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
        val totalInterestEarned by viewModel.totalInterestEarned

        val yearsToGrow by viewModel.yearsToGrow

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
            viewModel.calculateInvestment()
        }

        My1stMillionScreen(
            adsRemoved = adsRemoved,
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
                viewModel.openInInvestments {
                    nav.navigate(InvestmentScreenRoute)
                }
            },
            onBackPress = {
                nav.popBackStack()
            },
        )
    }
}
