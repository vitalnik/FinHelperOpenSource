package com.aripuca.finhelper.calculations

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GoalCalculatorTest {

    @Test
    fun yearsToGrowTest() {
        val calculator = GoalCalculator(
            initialPrincipalBalance = 1000000.0,
            interestRate = 1.0,
            regularAddition = 0.0,
            regularAdditionFrequency = 12.0,
            numberOfTimesInterestApplied = 12.0,
            goalAmount = 1000000.0,
        )
        calculator.calculate()

        assertEquals(0, calculator.yearsToGrow)
    }

    @Test
    fun yearsToGrowTest2() {
        val calculator = GoalCalculator(
            initialPrincipalBalance = 500000.0,
            interestRate = 3.5,
            regularAddition = 1000.0,
            regularAdditionFrequency = 12.0,
            numberOfTimesInterestApplied = 12.0,
            goalAmount = 1000000.0,
        )
        calculator.calculate()

        assertEquals(14, calculator.yearsToGrow)
    }

}