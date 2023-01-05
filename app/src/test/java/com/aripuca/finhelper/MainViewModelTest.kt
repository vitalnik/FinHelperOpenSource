package com.aripuca.finhelper

import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.billing.BillingManager
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MainViewModelTest {

    @MockK
    lateinit var billingManager: BillingManager

    @MockK
    lateinit var analyticsClient: AnalyticsClient

    lateinit var viewModel:MainViewModel

    @BeforeEach
    fun setUp() {

        coEvery { billingManager.productDetailsFlow } returns MutableStateFlow(listOf())
        coEvery { billingManager.purchasesFlow } returns MutableStateFlow(listOf())

        viewModel = MainViewModel(analyticsClient, billingManager)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun test() {
    }

}