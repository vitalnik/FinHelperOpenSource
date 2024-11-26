package com.aripuca.finhelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.billingclient.api.BillingFlowParams
import com.aripuca.finhelper.extensions.launchEmail
import com.aripuca.finhelper.services.billing.checkPurchases
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInterstitialAds()

        setContent {

            val navController = rememberNavController()

            val purchasesList by mainViewModel.purchasesFlow.collectAsStateWithLifecycle()

            val adsRemoved by remember {
                derivedStateOf {
                    purchasesList.checkPurchases() || mainViewModel.isTestDevice()
                }
            }

            FinHelperApp(
                navController = navController,
                mainViewModel = mainViewModel,
                showInterstitialAdAndThenNavigate = { ad, route ->
                    showInterstitialAdAndThenNavigate(
                        nav = navController,
                        interstitialAd = ad,
                        adsRemoved = adsRemoved,
                        route = route
                    )
                },
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
        route: Any,
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
                    mainViewModel.logInterstitialAdShown(route.toString())
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

