package com.aripuca.finhelper.calculations

import kotlin.math.pow

data class YearlyTableItem(
    val yearlyInvestment: Double,
    val totalInvestment: Double,
    val yearlyInterest: Double,
    val totalInterest: Double,
    val totalValue: Double,
)

class InvestmentCalculator(
    private val initialPrincipalBalance: Double,
    private val regularAddition: Double,
    private val regularAdditionFrequency: Double,
    private val interestRate: Double,
    private val numberOfTimesInterestApplied: Double,
    private val yearsToGrow: Int,
) {

    var totalValue: Double = 0.0
    var totalInterestEarned: Double = 0.0
    var totalInvestment: Double = 0.0
    var principalPercent: Double = 0.0

    var yearlyTable: MutableList<YearlyTableItem> = mutableListOf()

    fun calculate() {

        if (numberOfTimesInterestApplied == 0.0 || interestRate == 0.0) {
            totalValue = initialPrincipalBalance
            totalInvestment = initialPrincipalBalance
            totalInterestEarned = 0.0
            yearlyTable.clear()
            return
        }

        val nt = numberOfTimesInterestApplied * yearsToGrow
        val r = interestRate / 100
        val rDivByN = r / numberOfTimesInterestApplied

        if (regularAddition > 0) {

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

        } else {
            totalValue = initialPrincipalBalance * (1 + rDivByN).pow(nt)
            totalInvestment = initialPrincipalBalance
            totalInterestEarned = totalValue - initialPrincipalBalance
        }

        principalPercent = if (totalValue > 0) {
            totalInvestment * 100.0 / totalValue
        } else {
            0.0
        }

        populateYearlyTable()
    }

    private fun populateYearlyTable() {

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
    }

}