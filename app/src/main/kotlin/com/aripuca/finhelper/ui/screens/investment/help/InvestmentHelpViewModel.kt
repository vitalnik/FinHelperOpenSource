package com.aripuca.finhelper.ui.screens.investment.help

import androidx.lifecycle.ViewModel
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvestmentHelpViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    fun logScreenView() {
        analyticsClient.log(AnalyticsClient.INVESTMENT_HELP_SCREEN_VIEW)
    }
}