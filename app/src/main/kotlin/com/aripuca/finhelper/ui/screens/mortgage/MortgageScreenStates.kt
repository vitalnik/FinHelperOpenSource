package com.aripuca.finhelper.ui.screens.mortgage

import com.aripuca.finhelper.calculations.MortgageCalculationParams
import com.aripuca.finhelper.calculations.ScheduleItem
import com.aripuca.finhelper.ui.screens.common.InputFieldsUpdateType

data class MortgageScreenState(
    val adsRemoved: Boolean,
    val inputFields: MortgageInputFields,
    val calculatedFields: MortgageCalculatedFields,
)

data class MortgageInputFields(
    val updateType: InputFieldsUpdateType = InputFieldsUpdateType.INIT,
    val principalAmountInput: String,
    val interestRateInput: String,
    val numberOfYearsInput: String,
    val paymentsPerYearInput: String,
) {
    fun toCalculationParams() = MortgageCalculationParams(
        loanAmount = principalAmountInput.toDoubleOrNull() ?: 0.0,
        interestRate = interestRateInput.toDoubleOrNull() ?: 0.0,
        numberOfYears = numberOfYearsInput.toIntOrNull() ?: 0,
        paymentsPerYear = paymentsPerYearInput.toIntOrNull() ?: 0,
    )
}

data class MortgageCalculatedFields(
    val payment: String = "",
    val totalInterest: String = "",
    val totalPayments: String = "",
    val principalAmount: String = "",
    val principalPercent: Float = 0f,
    val paymentsSchedule: List<ScheduleItem> = listOf(),
    val yearSummary: List<ScheduleItem> = listOf(),
)
