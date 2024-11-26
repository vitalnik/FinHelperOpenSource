package com.aripuca.finhelper.calculations

import javax.inject.Inject
import kotlin.math.pow

data class YearlyTableItem(
    val yearlyInvestment: Double,
    val totalInvestment: Double,
    val yearlyInterest: Double,
    val totalInterest: Double,
    val totalValue: Double,
)

data class InvestmentCalculationParams(
    val initialPrincipalBalance: Double,
    val regularAddition: Double,
    val regularAdditionFrequency: Double,
    val interestRate: Double,
    val numberOfTimesInterestApplied: Double,
    val yearsToGrow: Int
)

data class InvestmentResults(
    val totalValue: Double = 0.0,
    val totalInterestEarned: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val principalPercent: Double = 0.0,
    val yearlyTable: List<YearlyTableItem> = listOf()
)

class InvestmentCalculator @Inject constructor() {

    fun calculate(
        calculationParams: InvestmentCalculationParams
    ): InvestmentResults {

        var totalValue: Double = 0.0
        var totalInterestEarned: Double = 0.0
        var totalInvestment: Double = 0.0
        var principalPercent: Double = 0.0
        var yearlyTable: List<YearlyTableItem> = listOf()

        with(calculationParams) {

            val nt = numberOfTimesInterestApplied * yearsToGrow
            val r = interestRate / 100
            val rDivByN = r / numberOfTimesInterestApplied

            if (interestRate > 0.0) {

                val principalBalance = initialPrincipalBalance

                totalValue = 0.0

                totalValue =
                    principalBalance * (1 + rDivByN).pow(nt) +
                            regularAddition * (regularAdditionFrequency / numberOfTimesInterestApplied) *
                            ((1 + rDivByN).pow(nt) - 1) / rDivByN

                totalInterestEarned =
                    totalValue - initialPrincipalBalance - regularAddition * regularAdditionFrequency * yearsToGrow

                totalInvestment =
                    initialPrincipalBalance + regularAddition * regularAdditionFrequency * yearsToGrow

                yearlyTable = populateYearlyTable(
                    initialPrincipalBalance,
                    regularAddition,
                    regularAdditionFrequency,
                    interestRate,
                    numberOfTimesInterestApplied,
                    yearsToGrow,
                )
            } else {
                totalValue = initialPrincipalBalance * (1 + rDivByN).pow(nt)
                totalInvestment = initialPrincipalBalance
                totalInterestEarned = totalValue - initialPrincipalBalance
                yearlyTable = emptyList()
            }

            principalPercent = if (totalValue > 0) {
                totalInvestment * 100.0 / totalValue
            } else {
                0.0
            }
        }

        return InvestmentResults(
            totalValue = totalValue,
            totalInterestEarned = totalInterestEarned,
            totalInvestment = totalInvestment,
            principalPercent = principalPercent,
            yearlyTable = yearlyTable
        )
    }

    private fun populateYearlyTable(
        initialPrincipalBalance: Double,
        regularAddition: Double,
        regularAdditionFrequency: Double,
        interestRate: Double,
        numberOfTimesInterestApplied: Double,
        yearsToGrow: Int,
    ): List<YearlyTableItem> {

        var yearlyTable: MutableList<YearlyTableItem> = mutableListOf()

        val nt = numberOfTimesInterestApplied
        val rn = interestRate / 100 / numberOfTimesInterestApplied

        var principalBalance = initialPrincipalBalance

        var totalInvestment = initialPrincipalBalance + regularAddition * regularAdditionFrequency
        var yearlyInvestment = initialPrincipalBalance + regularAddition * regularAdditionFrequency

        var yearlyInterest: Double
        var totalInterest = 0.0
        var totalValue: Double

        repeat(yearsToGrow) {

            totalValue =
                principalBalance * (1 + rn).pow(nt) + regularAddition * ((1 + rn).pow(nt) - 1) / rn

            val prevTotalInterest = totalInterest
            totalInterest = totalValue - totalInvestment
            yearlyInterest = totalInterest - prevTotalInterest

            yearlyTable.add(
                YearlyTableItem(
                    yearlyInvestment = yearlyInvestment,
                    totalInvestment = totalInvestment,
                    yearlyInterest = yearlyInterest,
                    totalInterest = totalInterest,
                    totalValue = totalValue
                )
            )

            principalBalance = (totalInvestment + totalInterest)

            totalInvestment += regularAddition * regularAdditionFrequency
            yearlyInvestment = regularAddition * regularAdditionFrequency
        }

        return yearlyTable
    }

}