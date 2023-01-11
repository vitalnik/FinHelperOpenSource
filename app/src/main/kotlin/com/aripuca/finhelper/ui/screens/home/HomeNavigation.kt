package com.aripuca.finhelper.ui.screens.home

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.aripuca.finhelper.R
import com.aripuca.finhelper.extensions.getVersionName
import com.google.accompanist.navigation.animation.composable

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    navigateToMy1stMillionScreen: () -> Unit = {}
) {
    composable(route = "home_screen") {

        val homeViewModel = hiltViewModel<HomeViewModel>()

        val context = LocalContext.current
        val versionName = stringResource(id = R.string.ver) + " " + context.getVersionName()

        LaunchedEffect(key1 = true) {
            homeViewModel.logScreenView()
        }

        HomeScreen(
            versionName = versionName,
            navigateToMortgage = {
                navController.navigate("mortgage_screen")
            },
            navigateToInvestment = {
                navController.navigate("investment_screen")
            },
            navigateToMy1stMillion = {
                navigateToMy1stMillionScreen()
                //navController.navigate("my1st_million_screen")
            },
            navigateToAbout = {
                navController.navigate("about_screen")
            }
        )
    }
}
