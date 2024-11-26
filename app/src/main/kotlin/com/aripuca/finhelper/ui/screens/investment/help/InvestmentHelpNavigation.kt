package com.aripuca.finhelper.ui.screens.investment.help

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.InvestmentHelpScreenRoute

fun NavGraphBuilder.investmentHelpScreen(
    nav: NavController
) {

    composable<InvestmentHelpScreenRoute> {

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
