package com.aripuca.finhelper.ui.screens.mortgage

import com.aripuca.finhelper.extensions.checkRange
import javax.inject.Inject

class MortgageInputFieldsValidator @Inject constructor() {

    fun validatePrincipalAmount(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 1_000_000_000.0)
    }

    fun validateNumberOfYears(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 35.0)
    }

    fun validatePaymentsPerYear(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 365.0)
    }

    fun validateInterestRate(textValue: String): Boolean {
        val value = textValue.toDoubleOrNull() ?: 0.0
        return value.checkRange(max = 100.0)
    }

}
