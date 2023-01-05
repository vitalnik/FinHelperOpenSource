package com.aripuca.finhelper.calculations

import kotlin.math.pow

class GoalCalculator(
    private val initialPrincipalBalance: Double,
    private val regularAddition: Double,
    private val regularAdditionFrequency: Double,
    private val interestRate: Double,
    private val numberOfTimesInterestApplied: Double,
    private val goalAmount: Double,
) {

    var totalValue: Double = 0.0
    var totalInterestEarned: Double = 0.0
    var totalInvestment: Double = 0.0
    var yearsToGrow = 0

    fun calculate() {

        yearsToGrow = -1

        if (numberOfTimesInterestApplied == 0.0 || interestRate == 0.0) {
            yearsToGrow = 100
            totalValue = initialPrincipalBalance
            totalInvestment = initialPrincipalBalance
            totalInterestEarned = 0.0
            return
        }

        do {

            yearsToGrow++

            val nt = numberOfTimesInterestApplied * yearsToGrow
            val r = interestRate / 100
            val rDivByN = r / numberOfTimesInterestApplied

            if (regularAddition > 0) {

                val principalBalance = initialPrincipalBalance

                totalValue =
                    principalBalance * (1 + rDivByN).pow(nt) +
                            regularAddition * ((1 + rDivByN).pow(nt) - 1) / rDivByN

                totalInterestEarned =
                    totalValue - initialPrincipalBalance - regularAddition * regularAdditionFrequency * yearsToGrow

                totalInvestment =
                    initialPrincipalBalance + regularAddition * regularAdditionFrequency * yearsToGrow

            } else {
                totalValue = initialPrincipalBalance * (1 + rDivByN).pow(nt)
                totalInvestment = initialPrincipalBalance
                totalInterestEarned = totalValue - initialPrincipalBalance
            }

            if (yearsToGrow >= 100) {
                break
            }

        } while (totalValue < goalAmount)

    }

}