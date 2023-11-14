package com.aripuca.finhelper.calculations

import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow

data class ScheduleItem(
    val principalPart: Double,
    val interestPart: Double,
    val loanRemainder: Double
)

data class MortgageCalculationResults(
    var totalInterest: Double = 0.0,
    var totalPayments: Double = 0.0,
    var paymentPerPeriod: Double = 0.0,
    var principalPercent: Double = 0.0,
    var paymentsSchedule: MutableList<ScheduleItem> = mutableListOf(),
    var yearSummary: MutableList<ScheduleItem> = mutableListOf(),
)

class MortgageCalculator @Inject constructor() {

    private fun calculatePaymentPerPeriod(
        loanAmount: Double,
        interestRate: Double,
        numberOfYears: Int,
        paymentsPerYear: Int,
    ): Double {
        val periodInterestRate = interestRate / 100 / paymentsPerYear
        val numberOfPayments = paymentsPerYear * numberOfYears

        val result = loanAmount *
                (periodInterestRate * (1 + periodInterestRate).pow(numberOfPayments)) /
                ((1 + periodInterestRate).pow(numberOfPayments) - 1)

        if (result.isNaN()) {
            return 0.0
        }

        return result
    }

    fun calculate(
        loanAmount: Double,
        interestRate: Double,
        numberOfYears: Int,
        paymentsPerYear: Int,
    ): MortgageCalculationResults {

        if (loanAmount == 0.0 || interestRate == 0.0 || numberOfYears == 0 || paymentsPerYear == 0) {
            return MortgageCalculationResults()
        }

        var totalInterest = 0.0
        var totalPayments = 0.0
        var principalPercent = 0.0
        var paymentPerPeriod = 0.0
        val paymentsSchedule = mutableListOf<ScheduleItem>()
        val yearSummary = mutableListOf<ScheduleItem>()

        val periodInterestRate = interestRate / 100 / paymentsPerYear
        var loanRemainder = loanAmount
        val numberOfPayments = paymentsPerYear * numberOfYears

        var yearInterest = 0.0
        var yearPrincipal = 0.0

        paymentPerPeriod = calculatePaymentPerPeriod(
            loanAmount = loanAmount,
            interestRate = interestRate,
            numberOfYears = numberOfYears,
            paymentsPerYear = paymentsPerYear,
        )

        repeat(numberOfPayments) { paymentIndex ->
            val interestPart = loanRemainder * periodInterestRate
            val principalPart = paymentPerPeriod - interestPart
            loanRemainder -= principalPart
            totalInterest += interestPart
            paymentsSchedule.add(
                ScheduleItem(
                    principalPart = principalPart,
                    interestPart = interestPart,
                    loanRemainder = abs(loanRemainder)
                )
            )

            yearInterest += interestPart
            yearPrincipal += principalPart

            if ((paymentIndex + 1) % paymentsPerYear == 0) {
                yearSummary.add(
                    ScheduleItem(
                        principalPart = yearPrincipal,
                        interestPart = yearInterest,
                        loanRemainder = loanRemainder
                    )
                )
                yearInterest = 0.0
                yearPrincipal = 0.0
            }
        }

        totalPayments = loanAmount + totalInterest

        principalPercent = if (totalPayments > 0) {
            loanAmount * 100.0 / totalPayments
        } else {
            0.0
        }

        return MortgageCalculationResults(
            totalInterest = totalInterest,
            totalPayments = totalPayments,
            paymentPerPeriod = paymentPerPeriod,
            principalPercent = principalPercent,
            paymentsSchedule = paymentsSchedule,
            yearSummary = yearSummary,
        )
    }
}