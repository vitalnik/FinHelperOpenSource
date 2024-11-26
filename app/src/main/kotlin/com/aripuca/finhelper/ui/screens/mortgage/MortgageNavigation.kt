package com.aripuca.finhelper.ui.screens.mortgage

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.MortgageAffordabilityScreenRoute
import com.aripuca.finhelper.MortgageScreenRoute
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.common.HistoryPanelEvents

fun NavGraphBuilder.mortgageScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
    navigateToHelp: () -> Unit = {},
) {
    composable<MortgageScreenRoute> {

        val viewModel = hiltViewModel<MortgageViewModel>()

        val purchasesList by mainViewModel.purchasesFlow.collectAsStateWithLifecycle()

        val adsRemoved by remember {
            derivedStateOf {
                purchasesList.checkPurchases() || mainViewModel.isTestDevice()
            }
        }

        val inputFields by viewModel.inputFieldsFlow.collectAsStateWithLifecycle()
        val calculatedFields by viewModel.calculatedFieldsFlow.collectAsStateWithLifecycle()
        val historyPanelState by viewModel.historyPanelFlow.collectAsStateWithLifecycle()

        val selectedHistoryItemIndex by derivedStateOf {
            historyPanelState.selectedHistoryItemIndex
        }

        val screenState = MortgageScreenState(
            adsRemoved = adsRemoved,
            inputFields = inputFields,
            calculatedFields = calculatedFields,
        )

        val screenEvents = createMortgageScreenEvents(
            viewModel = viewModel,
            navController = nav,
            navigateToHelp = navigateToHelp
        )
        val historyPanelEvents = createHistoryPanelEvents(viewModel)

        LaunchedEffect(key1 = true) {
            viewModel.logScreenView()
        }

        LaunchedEffect(key1 = selectedHistoryItemIndex) {
            viewModel.loadHistoryItem(selectedHistoryItemIndex)
        }

        MortgageScreen(
            screenState = screenState,
            screenEvents = screenEvents,
            historyPanelState = historyPanelState,
            historyPanelEvents = historyPanelEvents
        )
    }
}

fun createMortgageScreenEvents(
    viewModel: MortgageViewModel,
    navController: NavController,
    navigateToHelp: () -> Unit = {}
) = MortgageScreenEvents(
    onPrincipalAmountChanged = {
        viewModel.updatePrincipalAmount(it)
    },
    onInterestRateChanged = {
        viewModel.updateInterestRate(it)
    },
    onNumberOfYearsChanged = {
        viewModel.updateNumberOfYears(it)
    },
    onPaymentsPerYearChanged = {
        viewModel.updatePaymentsPerYear(it)
    },
    onHelpClick = {
        navigateToHelp()
    },
    onAffordabilityClicked = {
        navController.navigate(MortgageAffordabilityScreenRoute)
    },
    onPaymentScheduleClicked = {
        viewModel.logPaymentsScheduleClick()
    },
    onBackPress = {
        navController.popBackStack()
    }
)

fun createHistoryPanelEvents(viewModel: MortgageViewModel) = HistoryPanelEvents(
    onAddHistoryItem = {
        viewModel.addHistoryItem()
    },
    onSaveHistoryItem = {
        viewModel.saveHistoryItem()
    },
    onDeleteHistoryItem = {
        viewModel.deleteHistoryItem()
    },
    onLoadPrevHistoryItem = {
        viewModel.decrementSelectedHistoryItemIndex()
    },
    onLoadNextHistoryItem = {
        viewModel.incrementSelectedHistoryItemIndex()
    }
)
