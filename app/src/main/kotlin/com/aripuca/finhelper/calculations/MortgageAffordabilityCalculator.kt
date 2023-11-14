package com.aripuca.finhelper.calculations

import javax.inject.Inject
import kotlin.math.pow

data class AffordabilityResults(
    val loanAmount: Double,
    val monthlyPayment: Double,
    val purchasePrice: Double,
)

class MortgageAffordabilityCalculator @Inject constructor() {
    fun calculate(
        monthlyIncome: Double,
        monthlyDebtPayment: Double,
        downPayment: Double = 5.0,
        interestRate: Double = 5.0,
        numberOfYears: Int = 25
    ): AffordabilityResults {

        val paymentsPerYear = 12

        val periodInterestRate = interestRate / 100 / paymentsPerYear
        val numberOfPayments = paymentsPerYear * numberOfYears

        var loanAmount = 0.0
        var monthlyPayment = 0.0

        while (monthlyPayment + monthlyDebtPayment < monthlyIncome * 0.3) {
            monthlyPayment = loanAmount *
                    (periodInterestRate * (1 + periodInterestRate).pow(numberOfPayments)) /
                    ((1 + periodInterestRate).pow(numberOfPayments) - 1)
            if (monthlyPayment + monthlyDebtPayment < monthlyIncome * 0.3) {
                loanAmount += 100.0
            }
        }

        return AffordabilityResults(
            loanAmount = loanAmount,
            monthlyPayment = monthlyPayment,
            purchasePrice = loanAmount + downPayment
        )
    }

}