package com.aripuca.finhelper.ui.screens.investment

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aripuca.finhelper.R
import com.aripuca.finhelper.calculations.InvestmentCalculationParams
import com.aripuca.finhelper.calculations.YearlyTableItem
import com.aripuca.finhelper.ui.screens.common.InputFieldsUpdateType

enum class Frequency(val value: Int) {
    WEEKLY(52), MONTHLY(12), QUARTERLY(4), ANNUALLY(1);

    companion object {
        fun Int.getFrequency(): Frequency =
            when (this) {
                52 -> WEEKLY
                12 -> MONTHLY
                4 -> QUARTERLY
                else -> ANNUALLY
            }

        @Composable
        fun Frequency.getText(): String =
            when (this) {
                WEEKLY -> stringResource(R.string.weekly_option)
                MONTHLY -> stringResource(R.string.monthly_option)
                QUARTERLY -> stringResource(R.string.quarterly_option)
                ANNUALLY -> stringResource(R.string.annually_option)
            }

        @Composable
        fun Frequency.getText2(): String =
            when (this) {
                WEEKLY -> stringResource(R.string.weekly_option2)
                MONTHLY -> stringResource(R.string.monthly_option2)
                QUARTERLY -> stringResource(R.string.quarterly_option2)
                ANNUALLY -> stringResource(R.string.annually_option2)
            }
    }

}

data class InvestmentScreenState(
    val inputFields: InvestmentInputFields,
    val calculatedFields: InvestmentCalculatedFields,
    val adsRemoved: Boolean
)

data class InvestmentInputFields(
    val updateType: InputFieldsUpdateType = InputFieldsUpdateType.INIT,
    val initialInvestmentInput: String = "",
    val interestRateInput: String = "",
    val yearsToGrowInput: String = "",
    val regularContributionInput: String = "",
    val regularAdditionFrequencyInput: Int = 0
) {
    fun toCalculationParams() = InvestmentCalculationParams(
        initialPrincipalBalance = initialInvestmentInput.toDoubleOrNull() ?: 0.0,
        regularAddition = regularContributionInput.toDoubleOrNull() ?: 0.0,
        regularAdditionFrequency = regularAdditionFrequencyInput.toDouble(),
        interestRate = interestRateInput.toDoubleOrNull() ?: 0.0,
        numberOfTimesInterestApplied = regularAdditionFrequencyInput.toDouble(),
        yearsToGrow = yearsToGrowInput.toIntOrNull() ?: 0,
    )
}

data class InvestmentCalculatedFields(
    val totalInvestment: String = "",
    val totalInterestEarned: String = "",
    val totalValue: String = "",
    val yearlyTable: List<YearlyTableItem> = listOf(),
    val principalPercent: Double = 50.0
)
