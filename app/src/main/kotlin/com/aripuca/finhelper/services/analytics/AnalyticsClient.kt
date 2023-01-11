package com.aripuca.finhelper.services.analytics

interface AnalyticsClient {
    val isTestDevice: Boolean
    fun log(event: String)
    fun log(event: String, params: Map<String, String>)
    fun recordException(exception: Throwable)
}