package com.aripuca.finhelper.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.aripuca.finhelper.ui.preview.ScheduleItemsProvider
import com.aripuca.finhelper.ui.preview.YearSummaryProvider
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.mortgage.MortgageScreen
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import org.junit.Rule
import org.junit.Test

class MortgageScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mortgageScreenContentTest() {
        composeTestRule.setContent {
            FinHelperTheme {
                MortgageScreen(
                    adsRemoved = true,
                    payment = "$ 500.00",
                    totalInterest = "$ 1000.00",
                    totalPayments = "$ 10,0000.00",
                    principalPercent = 50f,
                    principalAmount = "$ 50,0000.00",
                    principalAmountInput = "500000.00",
                    interestRate = "2.5",
                    numberOfYears = "25",
                    paymentsPerYear = "12",
                    paymentsSchedule = ScheduleItemsProvider().values.first(),
                    yearSummary = YearSummaryProvider().values.first()
                )
            }
        }

        composeTestRule.onNodeWithTag("chart_title").assertIsDisplayed()

        composeTestRule.onNodeWithTag("principal_amount_input").assertIsDisplayed()
        composeTestRule.onNodeWithText("500000.00").assertIsDisplayed()

        composeTestRule.onNodeWithTag("interest_rate_input").assertIsDisplayed()
        composeTestRule.onNodeWithText("2.5").assertIsDisplayed()

        composeTestRule.onNodeWithTag("number_of_years_input").assertIsDisplayed()
        composeTestRule.onNodeWithText("25").assertIsDisplayed()

        composeTestRule.onNodeWithTag("payments_per_year_input").assertIsDisplayed()
        composeTestRule.onNodeWithText("12").assertIsDisplayed()

    }

    @Test
    fun historyTest() {
        composeTestRule.setContent {
            FinHelperTheme {
                MortgageScreen(
                    adsRemoved = true,
                    payment = "$ 500.00",
                    totalInterest = "$ 1000.00",
                    totalPayments = "$ 10,0000.00",
                    principalPercent = 50f,
                    principalAmount = "$ 50,0000.00",
                    principalAmountInput = "500000.00",
                    interestRate = "2.5",
                    numberOfYears = "25",
                    paymentsPerYear = "12",
                    paymentsSchedule = ScheduleItemsProvider().values.first(),
                    yearSummary = YearSummaryProvider().values.first(),
                    historyPanelState = HistoryPanelState(
                        selectedHistoryItemIndex = 0,
                        historyItemCount = 2,
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("1/2").assertIsDisplayed()


    }

}