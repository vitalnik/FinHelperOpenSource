package com.aripuca.finhelper.ui.screens.mortgage.affordability

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.ui.screens.mortgage.MortgageViewModel

fun NavGraphBuilder.mortgageAffordabilityScreen(
    nav: NavController,
    navigateToMortgage: () -> Unit = {},
) {
    composable("mortgage_affordability_screen") {

        //val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        val viewModel = hiltViewModel<MortgageAffordabilityViewModel>()

        val calculationResult by viewModel.calculationResult.collectAsState()

        val monthlyIncome by viewModel.monthlyIncome
        val monthlyDebtPayment by viewModel.monthlyDebtPayment
        val downPayment by viewModel.downPayment
        val interestRate by viewModel.interestRate
        val selectedYearIndex by viewModel.selectedYearIndex

        val canCalculate by viewModel.canCalculate

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
        }

        MortgageAffordabilityScreen(
            monthlyIncome = monthlyIncome,
            monthlyDebtPayment = monthlyDebtPayment,
            downPayment = downPayment,
            interestRate = interestRate,
            selectedYearIndex = selectedYearIndex,
            yearsList = viewModel.yearsList,
            calculatedLoanAmount = calculationResult?.loanAmount ?: 0.0,
            calculatedPurchasePrice = calculationResult?.purchasePrice ?: 0.0,
            calculatedMonthlyPayment = calculationResult?.monthlyPayment ?: 0.0,
            canCalculate = canCalculate,
            onMonthlyIncomeChanged = {
                viewModel.updateMonthlyIncome(it)
            },
            onMonthlyDebtPaymentChanged = {
                viewModel.updateMonthlyDebtPayment(it)
            },
            onDownPaymentChanged = {
                viewModel.updateDownPayment(it)
            },
            onInterestRateChanged = {
                viewModel.updateInterestRate(it)
            },
            onYearSelected = {
                viewModel.selectYear(it)
            },
            onCalculateClicked = {
                viewModel.calculate()
                focusManager.clearFocus()
            },
            onOpenInMortgageClicked = {
                viewModel.openInMortgage {
                    nav.popBackStack()
                }
            },
            onBackPress = {
                nav.popBackStack()
            },
        )
    }
}
