package com.aripuca.finhelper.calculations

import com.aripuca.finhelper.extensions.toCurrency
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class MortgageCalculatorTest {
    @Test
    fun paymentPerPeriodTest() {
        val mortgageCalculator = MortgageCalculator(
            loanAmount = 150000.0,
            interestRate = 3.5,
            numberOfYears = 25,
            paymentsPerYear = 12,
        )
        assertEquals("$${Typography.nbsp}750.94", mortgageCalculator.calculatePaymentPerPeriod().toCurrency())
    }
    @Test
    fun paymentPerPeriodTest2() {
        val mortgageCalculator = MortgageCalculator(
            loanAmount = 150000.0,
            interestRate = 3000.0,
            numberOfYears = 25,
            paymentsPerYear = 12,
        )
//        assertEquals("$${Typography.nbsp}750.94", mortgageCalculator.calculatePaymentPerPeriod())
//
//        mortgageCalculator.calculatePaymentsSchedule()
//
//        //assertEquals("$${Typography.nbsp}750.94", mortgageCalculator.totalPayments.toCurrency())
//        //assertEquals("0.01", mortgageCalculator.paymentsSchedule[0].principalPart)
//        assertEquals("0.01", mortgageCalculator.paymentsSchedule[0].interestPart)
//        assertEquals(150_000.0, mortgageCalculator.paymentsSchedule[0].loanRemainder)
    }
}