package com.aripuca.finhelper

import android.app.Application
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.billing.BillingManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    @Inject
    lateinit var analyticsClient: AnalyticsClient

    @Inject
    lateinit var billingManager: BillingManager

    override fun onCreate() {
        super.onCreate()

        analyticsClient.log(AnalyticsClient.APP_START)

        MobileAds.initialize(this)

        billingManager.startConnection()
    }
}