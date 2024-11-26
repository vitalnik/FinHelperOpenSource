package com.aripuca.finhelper

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingFlowParams
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.billing.BillingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val billingManager: BillingManager
) : ViewModel() {

    val productDetailsFlow = billingManager.productDetailsFlow

    val purchasesFlow = billingManager.purchasesFlow

    fun launchBillingFlow(activity: Activity, flowParams: BillingFlowParams): Int {
        return billingManager.launchBillingFlow(activity, flowParams)
    }

    fun logInterstitialAdShown(screen: String) {
        analyticsClient.log(AnalyticsClient.INTERSTITIAL_AD_SHOWN, mapOf("SCREEN" to screen))
    }

    fun isTestDevice():Boolean = analyticsClient.isTestDevice
}