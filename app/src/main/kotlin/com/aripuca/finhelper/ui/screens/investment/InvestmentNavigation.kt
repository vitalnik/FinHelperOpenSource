package com.aripuca.finhelper.ui.screens.investment

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aripuca.finhelper.InvestmentScreenRoute
import com.aripuca.finhelper.MainViewModel
import com.aripuca.finhelper.services.billing.checkPurchases
import com.aripuca.finhelper.ui.screens.common.HistoryPanelEvents

fun NavGraphBuilder.investmentScreen(
    nav: NavController,
    mainViewModel: MainViewModel,
    navigateToHelp: () -> Unit = {},
) {
    composable<InvestmentScreenRoute> {

        val viewModel = hiltViewModel<InvestmentViewModel>()

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

        val screenState = InvestmentScreenState(
            inputFields = inputFields,
            calculatedFields = calculatedFields,
            adsRemoved = adsRemoved
        )

        val screenEvents = createInvestmentScreenEvents(
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

        InvestmentScreen(
            screenState = screenState,
            screenEvents = screenEvents,
            historyPanelState = historyPanelState,
            historyPanelEvents = historyPanelEvents
        )
    }
}

fun createInvestmentScreenEvents(
    viewModel: InvestmentViewModel,
    navController: NavController,
    navigateToHelp: () -> Unit = {}
) = InvestmentScreenEvents(
    onPrincipalAmountChanged = {
        viewModel.updateInitialInvestment(it)
    },
    onRegularAdditionChanged = {
        viewModel.updateRegularContribution(it)
    },
    onInterestRateChanged = {
        viewModel.updateInterestRate(it)
    },
    onYearsToGrowChanged = {
        viewModel.updateYearsToGrow(it)
    },
    onRegularAdditionFrequencyChanged = {
        viewModel.updateRegularAdditionFrequency(it)
    },
    onHelpClick = {
        navigateToHelp()
    },
    onYearlyTableClicked = {
        viewModel.logYearlyTableClick()
    },
    onBackPress = {
        navController.popBackStack()
    }
)

fun createHistoryPanelEvents(viewModel: InvestmentViewModel) = HistoryPanelEvents(
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
