package com.aripuca.finhelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.android.billingclient.api.BillingFlowParams
import com.aripuca.finhelper.extensions.launchEmail
import com.aripuca.finhelper.extensions.navigationFlow
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.about.aboutScreen
import com.aripuca.finhelper.ui.screens.home.homeScreen
import com.aripuca.finhelper.ui.screens.investment.help.investmentHelpScreen
import com.aripuca.finhelper.ui.screens.investment.investmentScreen
import com.aripuca.finhelper.ui.screens.mortgage.help.mortgageHelpScreen
import com.aripuca.finhelper.ui.screens.mortgage.mortgageScreen
import com.aripuca.finhelper.ui.screens.my1stmillion.my1stMillionScreen
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var interstitialAdShown: Boolean = false

    private var investmentHelpScreenInterstitialAd: InterstitialAd? = null
    private var mortgageHelpScreenInterstitialAd: InterstitialAd? = null
    private var my1stMillionScreenInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInterstitialAds()

        setContent {
            val navController = rememberAnimatedNavController()

            val purchasesList by mainViewModel.purchasesFlow.collectAsState()

            val adsRemoved by remember {
                derivedStateOf {
                    purchasesList.checkPurchases() || mainViewModel.isTestDevice()
                }
            }

            CompositionLocalProvider(

            ) {
                FinHelperTheme {
                    Surface {
                        AnimatedNavHost(
                            navController = navController,
                            startDestination = "home_screen",
                        ) {

                            homeScreen(navController) {
                                showInterstitialAdAndThenNavigate(
                                    nav = navController,
                                    interstitialAd = my1stMillionScreenInterstitialAd,
                                    adsRemoved = adsRemoved,
                                    route = "my1st_million_screen"
                                )
                            }

                            navigationFlow(
                                startDestination = "about_screen",
                                route = "about_flow"
                            ) {
                                aboutScreen(
                                    nav = navController,
                                    mainViewModel = mainViewModel,
                                    launchEmail = {
                                        launchEmail(
                                            emailAddress = getString(R.string.support_email),
                                            subject = "Financial Helper Support Request",
                                            body = ""
                                        )
                                    },
                                    launchWeb = this@MainActivity::launchWeb,
                                    launchPurchaseFlow = {
                                        val productDetailsParamsList = listOf(
                                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(it)
                                                .build()
                                        )
                                        val flowParams = BillingFlowParams.newBuilder()
                                            .setProductDetailsParamsList(productDetailsParamsList)
                                            .build()
                                        mainViewModel.launchBillingFlow(
                                            this@MainActivity,
                                            flowParams
                                        )
                                    }
                                )
                            }

                            navigationFlow(
                                startDestination = "mortgage_screen",
                                route = "mortgage_flow"
                            ) {
                                mortgageScreen(navController, mainViewModel) {
                                    showInterstitialAdAndThenNavigate(
                                        nav = navController,
                                        interstitialAd = mortgageHelpScreenInterstitialAd,
                                        adsRemoved = adsRemoved,
                                        route = "mortgage_help_screen"
                                    )
                                }
                                mortgageHelpScreen(navController)
                            }

                            navigationFlow(
                                startDestination = "investment_screen",
                                route = "investment_flow"
                            ) {
                                investmentScreen(navController, mainViewModel) {
                                    showInterstitialAdAndThenNavigate(
                                        navController,
                                        interstitialAd = investmentHelpScreenInterstitialAd,
                                        adsRemoved = adsRemoved,
                                        route = "investment_help_screen"
                                    )
                                }
                                investmentHelpScreen(navController)
                            }

                            navigationFlow(
                                startDestination = "my1st_million_screen",
                                route = "my1st_million_flow"
                            ) {
                                my1stMillionScreen(navController, mainViewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupInterstitialAds() {

        InterstitialAd.load(
            this,
            getString(R.string.mortgage_help_screen_ad_unit_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mortgageHelpScreenInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mortgageHelpScreenInterstitialAd = interstitialAd
                }
            })

//    @SuppressWarnings("VisibleForTests")
        InterstitialAd.load(
            this,
            getString(R.string.investment_help_screen_ad_unit_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    investmentHelpScreenInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    investmentHelpScreenInterstitialAd = interstitialAd
                }
            })


        InterstitialAd.load(
            this,
            getString(R.string.my1st_million_screen_ad_unit_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    my1stMillionScreenInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    my1stMillionScreenInterstitialAd = interstitialAd
                }
            })

    }

    private fun showInterstitialAdAndThenNavigate(
        nav: NavController,
        interstitialAd: InterstitialAd?,
        adsRemoved: Boolean,
        route: String,
    ) {

        if (interstitialAdShown) {
            Log.d("TAG", ">>> Interstitial ad already shown")
        }

        if (interstitialAd != null && !adsRemoved && !interstitialAdShown && !BuildConfig.DEBUG) {
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    nav.navigate(route)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    nav.navigate(route)
                }

                override fun onAdShowedFullScreenContent() {
                    mainViewModel.logInterstitialAdShown(route)
                    interstitialAdShown = true
                }
            }
            interstitialAd.show(this)
        } else {
            nav.navigate(route)
        }
    }

    private fun launchWeb(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }
}

