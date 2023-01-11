package com.aripuca.finhelper.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.aripuca.finhelper.calculations.ScheduleItem

class ScheduleItemsProvider : PreviewParameterProvider<List<ScheduleItem>> {
    override val values: Sequence<List<ScheduleItem>>
        get() = sequenceOf(
            listOf(
                ScheduleItem(interestPart = 1000.0, principalPart = 2000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 1000.0, principalPart = 2000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 1000.0, principalPart = 2000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 1000.0, principalPart = 2000.0, loanRemainder = 3000.0),
            )
        )
}

class YearSummaryProvider : PreviewParameterProvider<List<ScheduleItem>> {
    override val values: Sequence<List<ScheduleItem>>
        get() = sequenceOf(
            listOf(
                ScheduleItem(interestPart = 1000.0, principalPart = 2000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 2000.0, principalPart = 3000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 3000.0, principalPart = 4000.0, loanRemainder = 3000.0),
                ScheduleItem(interestPart = 4000.0, principalPart = 5000.0, loanRemainder = 3000.0),
            )
        )
}