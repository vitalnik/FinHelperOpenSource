package com.aripuca.finhelper.ui.screens.investment.help

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

fun NavGraphBuilder.investmentHelpScreen(
    nav: NavController
) {

    composable("investment_help_screen") {

        val viewModel = hiltViewModel<InvestmentHelpViewModel>()

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
        }

        InvestmentHelpScreen(
            onBackPress = {
                nav.popBackStack()
            }
        )
    }
}
