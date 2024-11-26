package com.aripuca.finhelper.ui.screens.mortgage.help

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.MortgageHelpScreenRoute
import com.aripuca.finhelper.MortgageScreenRoute

fun NavGraphBuilder.mortgageHelpScreen(
    nav: NavController
) {
    composable<MortgageHelpScreenRoute> {

        val viewModel = hiltViewModel<MortgageHelpViewModel>()

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
        }

        MortgageHelpScreen(
            onBackPress = {
                nav.popBackStack()
            }
        )
    }
}
