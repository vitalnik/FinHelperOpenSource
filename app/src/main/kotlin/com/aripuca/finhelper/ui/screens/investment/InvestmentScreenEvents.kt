package com.aripuca.finhelper.ui.screens.investment

data class InvestmentScreenEvents(
    val onPrincipalAmountChanged: (String) -> Unit = {},
    val onRegularAdditionChanged: (String) -> Unit = {},
    val onInterestRateChanged: (String) -> Unit = {},
    val onYearsToGrowChanged: (String) -> Unit = {},
    val onRegularAdditionFrequencyChanged: (Frequency) -> Unit = {},
    val onHelpClick: () -> Unit = {},
    val onYearlyTableClicked: () -> Unit = {},
    val onBackPress: () -> Unit = {}
)

data class InputFieldsEvents(
    val onPrincipalAmountChanged: (String) -> Unit,
    val onInterestRateChanged: (String) -> Unit,
    val onYearsToGrowChanged: (String) -> Unit,
    val onRegularAdditionChanged: (String) -> Unit,
    val onRegularAdditionFrequencyChanged: (Frequency) -> Unit
)

fun InvestmentScreenEvents.toInputFieldEvents() = InputFieldsEvents(
    onPrincipalAmountChanged = onPrincipalAmountChanged,
    onInterestRateChanged = onInterestRateChanged,
    onYearsToGrowChanged = onYearsToGrowChanged,
    onRegularAdditionChanged = onRegularAdditionChanged,
    onRegularAdditionFrequencyChanged = onRegularAdditionFrequencyChanged
)
