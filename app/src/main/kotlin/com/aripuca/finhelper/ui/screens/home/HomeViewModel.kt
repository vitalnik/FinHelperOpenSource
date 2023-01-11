package com.aripuca.finhelper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.HOME_SCREEN_VIEW)
    }

}