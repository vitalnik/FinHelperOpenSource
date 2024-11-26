package com.aripuca.finhelper.ui.screens.investment

import com.aripuca.finhelper.extensions.checkRange
import javax.inject.Inject

class InvestmentInputFieldsValidator @Inject constructor() {

    fun validateInitialInvestment(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000_000.0)
    }

    fun validateRegularContribution(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000.0)
    }

    fun validateInterestRate(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

    fun validateYearsToGrow(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

}
