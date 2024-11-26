package com.aripuca.finhelper.calculations

import com.android.billingclient.api.Purchase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Calculation engine. (Refactoring ideas.)
 */
class CalcEngine @Inject constructor(): CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

//    private val _calculationResults: MutableStateFlow<Invest> = MutableStateFlow(mutableListOf())
//    val purchasesFlow = _purchasesFlow.asStateFlow()


}