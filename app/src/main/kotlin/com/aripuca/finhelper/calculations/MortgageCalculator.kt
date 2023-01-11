package com.aripuca.finhelper.calculations

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow

data class ScheduleItem(
    val principalPart: Double,
    val interestPart: Double,
    val loanRemainder: Double
)

class MortgageCalculator(
    val loanAmount: Double,
    val interestRate: Double,
    val numberOfYears: Int,
    val paymentsPerYear: Int,
) {

    var totalInterest: Double = 0.0
    var totalPayments: Double = 0.0
    var paymentPerPeriod: Double = 0.0
    var principalPercent: Double = 0.0
    var paymentsSchedule: MutableList<ScheduleItem> = mutableListOf()
    var yearSummary: MutableList<ScheduleItem> = mutableListOf()

    private fun calculatePaymentPerPeriod(): Double {
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

    fun calculate() {

        if (loanAmount == 0.0 || interestRate == 0.0 || numberOfYears == 0 || paymentsPerYear == 0) {
            paymentPerPeriod = 0.0
            totalPayments = 0.0
            totalInterest = 0.0
            paymentsSchedule.clear()
            yearSummary.clear()
            return
        }

        paymentPerPeriod = calculatePaymentPerPeriod()

        val periodInterestRate = interestRate / 100 / paymentsPerYear
        var loanRemainder = loanAmount
        val numberOfPayments = paymentsPerYear * numberOfYears

        var yearInterest = 0.0
        var yearPrincipal = 0.0

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

    }
}