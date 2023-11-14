package com.aripuca.finhelper.ui.screens.mortgage

data class MortgageScreenEvents(
    val onPrincipalAmountChanged: (String) -> Unit = {},
    val onInterestRateChanged: (String) -> Unit = {},
    val onNumberOfYearsChanged: (String) -> Unit = {},
    val onPaymentsPerYearChanged: (String) -> Unit = {},
    val onAffordabilityClicked: () -> Unit = {},
    val onHelpClick: () -> Unit = {},
    val onBackPress: () -> Unit = {}
)

data class InputFieldsEvents(
    val onPrincipalAmountChanged: (String) -> Unit,
    val onInterestRateChanged: (String) -> Unit,
    val onNumberOfYearsChanged: (String) -> Unit,
    val onPaymentsPerYearChanged: (String) -> Unit
)

fun MortgageScreenEvents.toInputFieldEvents() = InputFieldsEvents(
    onPrincipalAmountChanged = onPrincipalAmountChanged,
    onInterestRateChanged = onInterestRateChanged,
    onNumberOfYearsChanged = onNumberOfYearsChanged,
    onPaymentsPerYearChanged = onPaymentsPerYearChanged
)
