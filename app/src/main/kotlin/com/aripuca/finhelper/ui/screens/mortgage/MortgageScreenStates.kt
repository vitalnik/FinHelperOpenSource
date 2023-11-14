package com.aripuca.finhelper.ui.screens.mortgage

import com.aripuca.finhelper.calculations.ScheduleItem

data class MortgageScreenState(
    val adsRemoved: Boolean,
    val inputValid: Boolean,
    val payment: String,
    val totalInterest: String,
    val totalPayments: String,
    val principalPercent: Float,
    val principalAmount: String,
    val principalAmountInput: String,
    val interestRate: String,
    val numberOfYears: String,
    val paymentsPerYear: String,
    val paymentsSchedule: List<ScheduleItem> = listOf(),
    val yearSummary: List<ScheduleItem> = listOf(),
)

data class InputFieldsState(
    val principalAmountInput: String,
    val interestRate: String,
    val numberOfYears: String,
    val paymentsPerYear: String,
)

fun MortgageScreenState.toInputFieldState() = InputFieldsState(
    principalAmountInput = principalAmountInput,
    interestRate = interestRate,
    numberOfYears = numberOfYears,
    paymentsPerYear = paymentsPerYear
)
