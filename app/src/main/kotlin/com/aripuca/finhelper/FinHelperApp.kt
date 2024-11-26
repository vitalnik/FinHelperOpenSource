package com.aripuca.finhelper

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.android.billingclient.api.ProductDetails
import com.aripuca.finhelper.extensions.navigationFlow
import com.aripuca.finhelper.ui.screens.about.aboutScreen
import com.aripuca.finhelper.ui.screens.home.homeScreen
import com.aripuca.finhelper.ui.screens.investment.help.investmentHelpScreen
import com.aripuca.finhelper.ui.screens.investment.investmentScreen
import com.aripuca.finhelper.ui.screens.mortgage.affordability.mortgageAffordabilityScreen
import com.aripuca.finhelper.ui.screens.mortgage.help.mortgageHelpScreen
import com.aripuca.finhelper.ui.screens.mortgage.mortgageScreen
import com.aripuca.finhelper.ui.screens.my1stmillion.my1stMillionScreen
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.google.android.gms.ads.interstitial.InterstitialAd

var investmentHelpScreenInterstitialAd: InterstitialAd? = null
var mortgageHelpScreenInterstitialAd: InterstitialAd? = null
var my1stMillionScreenInterstitialAd: InterstitialAd? = null

@Composable
fun FinHelperApp(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    launchEmail: () -> Unit,
    launchWeb: (String) -> Unit,
    showInterstitialAdAndThenNavigate: (InterstitialAd?, Any) -> Unit,
    launchPurchaseFlow: (ProductDetails) -> Unit,
) {

    FinHelperTheme {
        Surface {
            NavHost(
                navController = navController,
                startDestination = HomeScreenRoute,
            ) {

                homeScreen(navController) {
                    showInterstitialAdAndThenNavigate(
                        my1stMillionScreenInterstitialAd,
                        My1stMillionScreenRoute
                    )
                }

                navigationFlow(
                    startDestination = AboutScreenRoute,
                    route = AboutFlow::class
                ) {
                    aboutScreen(
                        nav = navController,
                        mainViewModel = mainViewModel,
                        launchEmail = {
                            launchEmail()
                        },
                        launchWeb = {
                            launchWeb(it)
                        },
                        launchPurchaseFlow = {
                            launchPurchaseFlow(it)
                        }
                    )
                }

                navigationFlow(
                    startDestination = MortgageScreenRoute,
                    route = MortgageFlow::class
                ) {
                    mortgageScreen(navController, mainViewModel) {
                        showInterstitialAdAndThenNavigate(
                            mortgageHelpScreenInterstitialAd,
                            MortgageHelpScreenRoute
                        )
                    }
                    mortgageAffordabilityScreen(navController)
                    mortgageHelpScreen(navController)
                }

                navigationFlow(
                    startDestination = InvestmentScreenRoute,
                    route = InvestmentFlow::class
                ) {
                    investmentScreen(navController, mainViewModel) {
                        showInterstitialAdAndThenNavigate(
                            investmentHelpScreenInterstitialAd,
                            InvestmentHelpScreenRoute
                        )
                    }
                    investmentHelpScreen(navController)
                }

                navigationFlow(
                    startDestination = My1stMillionScreenRoute,
                    route = My1stMillionFlow::class
                ) {
                    my1stMillionScreen(navController, mainViewModel)
                }
            }
        }
    }
}