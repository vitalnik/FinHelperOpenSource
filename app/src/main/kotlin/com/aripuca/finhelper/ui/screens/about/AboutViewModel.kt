package com.aripuca.finhelper.ui.screens.about

import androidx.lifecycle.ViewModel
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
) : ViewModel() {

    fun logScreenView() {
        analyticsClient.log(AnalyticsClient.ABOUT_SCREEN_VIEW)
    }

    fun logSupportEmailClick() {
        analyticsClient.log(AnalyticsClient.ABOUT_SCREEN_SUPPORT_EMAIL_CLICK)
    }

    fun logGitHubLinkClick() {
        analyticsClient.log(AnalyticsClient.ABOUT_SCREEN_GITHUB_LINK_CLICK)
    }

    fun logRemoveAdsPurchaseStart() {
        analyticsClient.log(AnalyticsClient.REMOVE_ADS_PURCHASE_START)
    }

    fun logBuyMeCoffeePurchaseStart() {
        analyticsClient.log(AnalyticsClient.BUY_ME_COFFEE_PURCHASE_START)
    }


}