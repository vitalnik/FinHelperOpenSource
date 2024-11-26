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
        //Log.d("TAG", ">>> Logging event: $event")
        if (analyticsCollectionEnabled) {
            analytics.logEvent(event, null)
        }
    }

    override fun log(event: String, params: Map<String, String>) {
        //Log.d("TAG", ">>> Logging event: $event Params: $params")
        if (analyticsCollectionEnabled) {
            analytics.logEvent(event, params.toBundle())
        }
    }

    override fun recordException(exception: Throwable) {
        crashlytics.recordException(exception)
    }

}