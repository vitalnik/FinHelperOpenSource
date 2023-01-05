package com.aripuca.finhelper.ui.screens.home

import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HomeViewModel

    @MockK
    lateinit var analyticsClient: AnalyticsClient

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        viewModel = HomeViewModel(analyticsClient)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun testAnalytics() {

        coEvery { analyticsClient.log(any()) } returns Unit

        viewModel.logScreenView()

        coVerify { analyticsClient.log(FirebaseAnalyticsClient.HOME_SCREEN_VIEW) }
    }


}