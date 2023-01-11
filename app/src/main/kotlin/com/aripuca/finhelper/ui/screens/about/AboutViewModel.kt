package com.aripuca.finhelper.ui.screens.about

import androidx.lifecycle.ViewModel
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val analyticsClient: FirebaseAnalyticsClient,
) : ViewModel() {

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.ABOUT_SCREEN_VIEW)
    }

    fun logSupportEmailClick() {
        analyticsClient.log(FirebaseAnalyticsClient.ABOUT_SCREEN_SUPPORT_EMAIL_CLICK)
    }

    fun logRemoveAdsPurchaseStart() {
        analyticsClient.log(FirebaseAnalyticsClient.REMOVE_ADS_PURCHASE_START)
    }

    fun logBuyMeCoffeePurchaseStart() {
        analyticsClient.log(FirebaseAnalyticsClient.BUY_ME_COFFEE_PURCHASE_START)
    }


}