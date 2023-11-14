package com.aripuca.finhelper.calculations

import javax.inject.Inject
import kotlin.math.pow

data class GoalResults(
    var totalValue: Double = 0.0,
    var totalInterestEarned: Double = 0.0,
    var totalInvestment: Double = 0.0,
    var yearsToGrow: Int = 0
)

class GoalCalculator @Inject constructor() {

    private var totalValue: Double = 0.0
    private var totalInterestEarned: Double = 0.0
    private var totalInvestment: Double = 0.0
    private var yearsToGrow: Int = 0

    fun calculate(
        initialPrincipalBalance: Double,
        regularAddition: Double,
        regularAdditionFrequency: Double,
        interestRate: Double,
        numberOfTimesInterestApplied: Double,
        goalAmount: Double
    ): GoalResults {

        yearsToGrow = -1

        if (numberOfTimesInterestApplied == 0.0 || interestRate == 0.0) {

            yearsToGrow = 100
            totalValue = initialPrincipalBalance
            totalInvestment = initialPrincipalBalance
            totalInterestEarned = 0.0

            return GoalResults(
                totalValue = totalValue,
                totalInterestEarned = totalInterestEarned,
                totalInvestment = totalInvestment,
                yearsToGrow = yearsToGrow,
            )
        }

        do {

            yearsToGrow++

            val nt = numberOfTimesInterestApplied * yearsToGrow
            val r = interestRate / 100
            val rDivByN = r / numberOfTimesInterestApplied

            if (regularAddition > 0) {
                totalValue =
                    initialPrincipalBalance * (1 + rDivByN).pow(nt) +
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

        return GoalResults(
            totalValue = totalValue,
            totalInterestEarned = totalInterestEarned,
            totalInvestment = totalInvestment,
            yearsToGrow = yearsToGrow,
        )

    }

}