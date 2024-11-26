package com.aripuca.finhelper.services.analytics

interface AnalyticsClient {

    val isTestDevice: Boolean

    fun log(event: String)

    fun log(event: String, params: Map<String, String>)

    fun recordException(exception: Throwable)

    companion object {
        const val APP_START = "APP_START"

        const val HOME_SCREEN_VIEW = "HOME_SCREEN_VIEW"

        const val MORTGAGE_SCREEN_VIEW = "MORTGAGE_SCREEN_VIEW"
        const val MORTGAGE_HELP_SCREEN_VIEW = "MORTGAGE_HELP_SCREEN_VIEW"
        const val MORTGAGE_HISTORY_SCROLL = "MORTGAGE_HISTORY_SCROLL"
        const val MORTGAGE_HISTORY_ADDED = "MORTGAGE_HISTORY_ADDED"
        const val MORTGAGE_HISTORY_DELETED = "MORTGAGE_HISTORY_DELETED"
        const val MORTGAGE_HISTORY_SAVED = "MORTGAGE_HISTORY_SAVED"
        const val MORTGAGE_PAYMENTS_SCHEDULE_CLICK = "MORTGAGE_PAYMENTS_SCHEDULE_CLICK"

        const val AFFORDABILITY_SCREEN_VIEW = "AFFORDABILITY_SCREEN_VIEW"
        const val AFFORDABILITY_CALCULATE_CLICK = "AFFORDABILITY_CALCULATE_CLICK"
        const val AFFORDABILITY_OPEN_IN_MORTGAGE_CLICK = "AFFORDABILITY_OPEN_IN_MORTGAGE_CLICK"

        const val INVESTMENT_SCREEN_VIEW = "INVESTMENT_SCREEN_VIEW"
        const val INVESTMENT_HELP_SCREEN_VIEW = "INVESTMENT_HELP_SCREEN_VIEW"
        const val INVESTMENT_HISTORY_SCROLL = "INVESTMENT_HISTORY_SCROLL"
        const val INVESTMENT_HISTORY_ADDED = "INVESTMENT_HISTORY_ADDED"
        const val INVESTMENT_HISTORY_DELETED = "INVESTMENT_HISTORY_DELETED"
        const val INVESTMENT_HISTORY_SAVED = "INVESTMENT_HISTORY_SAVED"
        const val INVESTMENT_YEARLY_TABLE_CLICK = "INVESTMENT_YEARLY_TABLE_CLICK"

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