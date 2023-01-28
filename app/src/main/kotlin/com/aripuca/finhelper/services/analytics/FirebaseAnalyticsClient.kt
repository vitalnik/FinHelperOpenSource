package com.aripuca.finhelper.services.analytics

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.aripuca.finhelper.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

fun Map<String, String>.toBundle(): Bundle {
    val bundle = Bundle()
    this.forEach {
        bundle.putString(it.key, it.value)
    }
    return bundle
}

@Singleton
class FirebaseAnalyticsClient @Inject constructor(
    @ApplicationContext val context: Context
) : AnalyticsClient {

    // backing property
    private val _isTestDevice =
        Settings.System.getString(context.contentResolver, "firebase.test.lab") == "true"

    private var analyticsCollectionEnabled: Boolean = !_isTestDevice && !BuildConfig.DEBUG

    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val analytics = FirebaseAnalytics.getInstance(context)

    init {
        // turning off analytics for Firebase test sessions
        analytics.setAnalyticsCollectionEnabled(analyticsCollectionEnabled)
    }

    override val isTestDevice: Boolean
        get() = _isTestDevice

    override fun log(event: String) {
        Log.d("TAG", ">>> Logging event: $event")
        if (analyticsCollectionEnabled) {
            analytics.logEvent(event, null)
        }
    }

    override fun log(event: String, params: Map<String, String>) {
        Log.d("TAG", ">>> Logging event: $event Params: $params")
        if (analyticsCollectionEnabled) {
            analytics.logEvent(event, params.toBundle())
        }
    }

    override fun recordException(exception: Throwable) {
        crashlytics.recordException(exception)
    }

    companion object {
        const val APP_START = "APP_START"

        const val HOME_SCREEN_VIEW = "HOME_SCREEN_VIEW"

        const val MORTGAGE_SCREEN_VIEW = "MORTGAGE_SCREEN_VIEW"
        const val MORTGAGE_HELP_SCREEN_VIEW = "MORTGAGE_HELP_SCREEN_VIEW"
        const val MORTGAGE_HISTORY_SCROLL = "MORTGAGE_HISTORY_SCROLL"
        const val MORTGAGE_HISTORY_ADDED = "MORTGAGE_HISTORY_ADDED"
        const val MORTGAGE_HISTORY_DELETED = "MORTGAGE_HISTORY_DELETED"
        const val MORTGAGE_HISTORY_SAVED = "MORTGAGE_HISTORY_SAVED"
        
        const val INVESTMENT_SCREEN_VIEW = "INVESTMENT_SCREEN_VIEW"
        const val INVESTMENT_HELP_SCREEN_VIEW = "INVESTMENT_HELP_SCREEN_VIEW"
        const val INVESTMENT_HISTORY_SCROLL = "INVESTMENT_HISTORY_SCROLL"
        const val INVESTMENT_HISTORY_ADDED = "INVESTMENT_HISTORY_ADDED"
        const val INVESTMENT_HISTORY_DELETED = "INVESTMENT_HISTORY_DELETED"
        const val INVESTMENT_HISTORY_SAVED = "INVESTMENT_HISTORY_SAVED"

        const val MY1ST_MILLION_SCREEN_VIEW = "MY1ST_MILLION_SCREEN_VIEW"
        const val MY1ST_MILLION_OPEN_IN_INVESTMENTS_CLICK = "MY1ST_MILLION_OPEN_IN_INVESTMENTS_CLICK"

        const val ABOUT_SCREEN_VIEW = "ABOUT_SCREEN_VIEW"
        const val ABOUT_SCREEN_SUPPORT_EMAIL_CLICK = "ABOUT_SCREEN_SUPPORT_EMAIL_CLICK"
        const val ABOUT_SCREEN_GITHUB_LINK_CLICK = "ABOUT_SCREEN_GITHUB_LINK_CLICK"

        const val BILLING_SERVICE_CONNECTION_START = "BILLING_SERVICE_CONNECTION_START"
        const val BILLING_SERVICE_CONNECTED = "BILLING_SERVICE_CONNECTED"
        const val BILLING_SERVICE_CONNECTION_ERROR = "BILLING_SERVICE_CONNECTION_ERROR"
        const val BILLING_SERVICE_CONNECTION_UNAVAILABLE = "BILLING_SERVICE_CONNECTION_UNAVAILABLE"
        const val BILLING_SERVICE_DISCONNECTED = "BILLING_CONNECTION_SUCCESS"

        const val REMOVE_ADS_PURCHASE_START = "REMOVE_ADS_PURCHASE_START"
        const val REMOVE_ADS_PURCHASE_SUCCESS = "REMOVE_ADS_PURCHASE_SUCCESS"

        const val BUY_ME_COFFEE_PURCHASE_START = "BUY_ME_COFFEE_PURCHASE_START"
        const val BUY_ME_COFFEE_PURCHASE_SUCCESS = "BUY_ME_COFFEE_PURCHASE_SUCCESS"

        const val INAPP_PURCHASE_ERROR = "INAPP_PURCHASE_ERROR"

        const val INTERSTITIAL_AD_SHOWN = "INTERSTITIAL_AD_SHOWN"
    }
}