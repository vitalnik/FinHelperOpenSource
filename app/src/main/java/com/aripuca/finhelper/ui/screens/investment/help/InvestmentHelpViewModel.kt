package com.aripuca.finhelper.ui.screens.investment.help

import androidx.lifecycle.ViewModel
import com.aripuca.finhelper.services.LocalStorage
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvestmentHelpViewModel @Inject constructor(
    private val analyticsClient: FirebaseAnalyticsClient
) : ViewModel() {

    fun logScreenView() {
        analyticsClient.log(FirebaseAnalyticsClient.INVESTMENT_HELP_SCREEN_VIEW)
    }
}