package com.aripuca.finhelper.ui.screens.about

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.R
import com.aripuca.finhelper.extensions.getVersionName
import com.aripuca.finhelper.services.billing.checkBuyMeCoffeePurchase
import com.aripuca.finhelper.services.billing.checkRemoveAdsPurchase
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import com.android.billingclient.api.ProductDetails
import com.aripuca.finhelper.AboutScreenRoute

fun NavGraphBuilder.aboutScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
    launchEmail: () -> Unit,
    launchWeb: (url: String) -> Unit = {},
    launchPurchaseFlow: (ProductDetails) -> Unit
) {

    composable<AboutScreenRoute> {

        val viewModel = hiltViewModel<AboutViewModel>()
        //  val homeViewModel =
        //     hiltViewModel<HomeViewModel>(remember { nav.getBackStackEntry(route = MAIN_GRAPH) })

        val context = LocalContext.current
        val versionName = stringResource(id = R.string.ver) + " " + context.getVersionName()

        val productDetailsList by mainViewModel.productDetailsFlow.collectAsStateWithLifecycle()
        val purchasesList by mainViewModel.purchasesFlow.collectAsStateWithLifecycle()

        val removeAdsPurchased by remember {
            derivedStateOf {
                purchasesList.checkRemoveAdsPurchase()
            }
        }

        val buyMeCoffeePurchased by remember {
            derivedStateOf {
                purchasesList.checkBuyMeCoffeePurchase()
            }
        }

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
        }

        AboutScreen(
            versionName = versionName,
            removeAdsPurchased = removeAdsPurchased,
            buyMeCoffeePurchased = buyMeCoffeePurchased,
            productDetailsList = productDetailsList,
            onBackPress = {
                nav.popBackStack()
            },
            onLaunchEmail = {
                viewModel.logSupportEmailClick()
                launchEmail()
            },
            onOpenGitHub = {
                viewModel.logGitHubLinkClick()
                launchWeb("https://github.com/vitalnik/FinHelperOpenSource")
            },
            onRemoveAds = {
                viewModel.logRemoveAdsPurchaseStart()
                launchPurchaseFlow(it)
            },
            onBuyMeCoffee = {
                viewModel.logBuyMeCoffeePurchaseStart()
                launchPurchaseFlow(it)
            }
        )
    }
}
