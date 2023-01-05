package com.aripuca.finhelper.calculations

import kotlin.math.abs
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
    var paymentsSchedule: MutableList<ScheduleItem> = mutableListOf()
    var yearSummary: MutableList<ScheduleItem> = mutableListOf()

    fun calculatePaymentPerPeriod(): Double {
        val periodInterestRate = interestRate / 100 / paymentsPerYear
        val numberOfPayments = paymentsPerYear * numberOfYears

        if (listOf(
                paymentsPerYear, interestRate,
                numberOfYears, loanAmount
            ).any { it == 0.0 }
        ) {
            return 0.0
        }

        val result = loanAmount *
                (periodInterestRate * (1 + periodInterestRate).pow(numberOfPayments)) /
                ((1 + periodInterestRate).pow(numberOfPayments) - 1)

        if (result.isNaN()) {
            return 0.0
        }
        return result
    }

    fun calculatePaymentsSchedule() {

        val paymentPerPeriod = calculatePaymentPerPeriod()

        val periodInterestRate = interestRate / 100 / paymentsPerYear
        var loanRemainder = loanAmount
        val numberOfPayments = paymentsPerYear * numberOfYears

        totalInterest = 0.0
        totalPayments = 0.0
        paymentsSchedule.clear()

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
    }
}