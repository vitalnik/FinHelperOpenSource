package com.aripuca.finhelper.ui.screens.mortgage

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.BuildConfig
import com.aripuca.finhelper.R
import com.aripuca.finhelper.calculations.ScheduleItem
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.ui.components.AdMobView
import com.aripuca.finhelper.ui.components.buttons.CenteredButton
import com.aripuca.finhelper.ui.components.buttons.CenteredTextButton
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.charts.InterestProgress
import com.aripuca.finhelper.ui.components.input.TextFieldRow
import com.aripuca.finhelper.ui.components.layout.HorizontalSpacer
import com.aripuca.finhelper.ui.components.layout.LabelRow
import com.aripuca.finhelper.ui.components.layout.TableCell
import com.aripuca.finhelper.ui.components.layout.TableHeaderCell
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.components.text.HeaderText
import com.aripuca.finhelper.ui.preview.ScheduleItemsProvider
import com.aripuca.finhelper.ui.preview.YearSummaryProvider
import com.aripuca.finhelper.ui.screens.common.HistoryPanel
import com.aripuca.finhelper.ui.screens.common.HistoryPanelEvents
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.common.TopAppBarRow
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.aripuca.finhelper.ui.theme.LocalDimensions
import com.aripuca.finhelper.ui.theme.bottomSheetPickHeight
import com.aripuca.finhelper.ui.theme.interestDark
import com.aripuca.finhelper.ui.theme.interestLight
import com.aripuca.finhelper.ui.theme.principalDark
import com.aripuca.finhelper.ui.theme.principalLight
import com.aripuca.finhelper.ui.theme.progress1Dark
import com.aripuca.finhelper.ui.theme.progress1Light
import com.aripuca.finhelper.ui.theme.progressOutlineDark
import com.aripuca.finhelper.ui.theme.progressOutlineLight
import com.aripuca.finhelper.ui.theme.totalDark
import com.aripuca.finhelper.ui.theme.totalLight
import com.himanshoe.charty.bar.StackedBarChart
import com.himanshoe.charty.bar.config.BarConfigDefaults
import com.himanshoe.charty.bar.model.StackedBarData
import com.himanshoe.charty.common.axis.AxisConfigDefaults
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MortgageScreen(
    screenState: MortgageScreenState,
    screenEvents: MortgageScreenEvents,
    historyPanelState: HistoryPanelState,
    historyPanelEvents: HistoryPanelEvents,
) {

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    LaunchedEffect(key1 = true) {
        scaffoldState.bottomSheetState.hide()
    }

    BottomSheetScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarRow(
                        title = stringResource(id = R.string.mortgage_calculator),
                        helpContentDescription = stringResource(id = R.string.mortgage_help),
                        onHelpClick = screenEvents.onHelpClick
                    )
                },
                navigationIcon = {
                    NavigationIcon {
                        screenEvents.onBackPress()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.9f),
            ) {
                PaymentsSchedule(screenState)
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = bottomSheetPickHeight,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {

            LazyColumn(state = listState, modifier = Modifier.weight(weight = 1f, fill = true)) {

                item {
                    VerticalSpacer()
                    InputFields(
                        state = screenState.inputFields,
                        events = screenEvents.toInputFieldEvents()
                    )

                    CenteredTextButton(text = stringResource(id = R.string.how_much_can_i_afford)) {
                        screenEvents.onAffordabilityClicked()
                    }
                }

                item {
                    HistoryPanel(
                        state = historyPanelState,
                        events = historyPanelEvents
                    )
                }

                item {
                    MortgageChart(screenState.calculatedFields.yearSummary)
                }

                item {
                    with(screenState.calculatedFields) {
                        CalculatedFields(
                            principalPercent,
                            payment,
                            principalAmount,
                            totalInterest,
                            totalPayments
                        )
                    }

                    if (!scaffoldState.bottomSheetState.isVisible) {
                        VerticalSpacer()
                        CenteredButton(text = stringResource(id = R.string.payments_schedule)) {
                            screenEvents.onPaymentScheduleClicked()
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                        VerticalSpacer()
                    }
                }

                item {
                    VerticalSpacer()
                }
            }

            if (!screenState.adsRemoved && !BuildConfig.DEBUG) {
                AdMobView(adUnitId = stringResource(id = R.string.mortgage_screen_banner_ad_unit_id))
            }
        }
    }
}

@Composable
private fun MortgageChart(yearSummary: List<ScheduleItem>) {

    val stackBarData = mutableListOf<StackedBarData>()

    yearSummary.forEachIndexed { index, scheduleItem ->
        val yValue = mutableListOf<Float>()
        yValue.add(scheduleItem.principalPart.toFloat())
        yValue.add(scheduleItem.interestPart.toFloat())

        stackBarData.add(StackedBarData(xValue = index + 1, yValue = yValue))
    }

    if (stackBarData.isEmpty()) {
        return
    }

    Column {

        VerticalSpacer()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mortgage_chart_title"),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.principal),
                modifier = Modifier,
                color = if (isSystemInDarkTheme()) principalDark else principalLight,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(id = R.string.vs),
                modifier = Modifier.padding(horizontal = 8.dp),
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(id = R.string.interest),
                modifier = Modifier,
                color = if (isSystemInDarkTheme()) interestDark else interestLight,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
        }

        VerticalSpacer(8.dp)

        Box(modifier = Modifier.fillMaxWidth()) {
            StackedBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                colors = listOf(
                    if (isSystemInDarkTheme()) principalDark else principalLight,
                    if (isSystemInDarkTheme()) interestDark else interestLight,
                ),
                stackBarData = stackBarData,
                axisConfig = AxisConfigDefaults.axisConfigDefaults2(
                    isSystemInDarkTheme()
                ),
                barConfig = BarConfigDefaults.barConfigDimesDefaults(),
            )
        }
    }
}

@Composable
private fun InputFields(
    state: MortgageInputFields,
    events: InputFieldsEvents,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextFieldRow(
            label = stringResource(R.string.principal_amount_label),
            value = state.principalAmountInput,
            onValueChanged = events.onPrincipalAmountChanged,
            testTag = "principal_amount_input",
        )
        HorizontalSpacer()
        TextFieldRow(
            label = "Interest rate, %",
            value = state.interestRateInput,
            onValueChanged = events.onInterestRateChanged,
            testTag = "interest_rate_input",
        )
    }

    VerticalSpacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextFieldRow(
            label = stringResource(R.string.amortization_years),
            value = state.numberOfYearsInput,
            onValueChanged = events.onNumberOfYearsChanged,
            testTag = "number_of_years_input",
        )
        HorizontalSpacer()
        TextFieldRow(
            label = stringResource(R.string.payments_per_year),
            value = state.paymentsPerYearInput,
            onValueChanged = events.onPaymentsPerYearChanged,
            testTag = "payments_per_year_input",
        )
    }
}

@Composable
private fun PaymentsScheduleRow(
    index: Int,
    item: ScheduleItem,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        TableCell(text = (index + 1).toString(), weight = 1f)
        HorizontalSpacer(8.dp)
        TableCell(text = item.principalPart.toCurrency())
        HorizontalSpacer(8.dp)
        TableCell(text = item.interestPart.toCurrency())
        HorizontalSpacer(8.dp)
        TableCell(text = item.loanRemainder.toCurrency())
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun CalculatedFields(
    principalPercent: Float,
    payment: String,
    principalAmount: String,
    totalInterest: String,
    totalPayments: String
) {

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            VerticalSpacer()

            InterestProgress(principalPercent.toDouble())

            VerticalSpacer()

            LabelRow(label = stringResource(R.string.payment_amount), value = payment)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LabelRow(
                label = stringResource(R.string.principal_amount),
                value = principalAmount,
                color = if (isSystemInDarkTheme()) principalDark else principalLight
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LabelRow(
                label = stringResource(R.string.total_interest),
                value = totalInterest,
                color = if (isSystemInDarkTheme()) interestDark else interestLight
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LabelRow(
                label = stringResource(R.string.total_payments),
                value = totalPayments,
                color = if (isSystemInDarkTheme()) totalDark else totalLight
            )
        }
    }
}

@Composable
private fun PaymentsScheduleHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = if (isSystemInDarkTheme()) 0.25f else 0.5f
                    )
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            TableHeaderCell(text = "#", weight = 1f)
            HorizontalSpacer(8.dp)
            TableHeaderCell(text = stringResource(R.string.principal))
            HorizontalSpacer(8.dp)
            TableHeaderCell(text = stringResource(R.string.interest))
            HorizontalSpacer(8.dp)
            TableHeaderCell(text = stringResource(R.string.remainder))
        }
        HorizontalDivider()
    }
}

@Composable
private fun PaymentsSchedule(
    screenState: MortgageScreenState,
) {
    HeaderText(text = stringResource(R.string.payments_schedule))
    PaymentsScheduleHeader()

    LazyColumn(
        state = rememberLazyListState(),
    ) {
        screenState.calculatedFields.paymentsSchedule.forEachIndexed { index, item ->
            item(key = "ScheduleRow$index") {
                PaymentsScheduleRow(
                    index = index,
                    item = item,
                )
            }
            item(key = "ScheduleYearSummaryRow$index") {
                PaymentsScheduleYearSummaryRow(index, screenState)
            }
        }
        item {
            VerticalSpacer(height = 32.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentsSchedulePreview() {
    FinHelperTheme {
        Column {
            PaymentsSchedule(
                screenState = MortgageScreenState(
                    adsRemoved = false,
                    inputFields = MortgageInputFields(
                        principalAmountInput = "500000.00",
                        interestRateInput = "2.5",
                        numberOfYearsInput = "25",
                        paymentsPerYearInput = "12"
                    ),
                    calculatedFields = MortgageCalculatedFields(
                        payment = "$500.00",
                        totalInterest = "$1000.00",
                        totalPayments = "$100000.00",
                        principalPercent = 50f,
                        principalAmount = "$500000.00",
                        paymentsSchedule = ScheduleItemsProvider().values.first(),
                        yearSummary = YearSummaryProvider().values.first()
                    )
                )
            )
        }
    }
}

@Composable
private fun PaymentsScheduleYearSummaryRow(index: Int, screenState: MortgageScreenState) {

    val yearIndex = (index / screenState.inputFields.paymentsPerYearInput.toInt())

    if ((index + 1) % (screenState.inputFields.paymentsPerYearInput.toIntOrNull() ?: 1) == 0) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(
                    color = if (isSystemInDarkTheme()) Color.LightGray.copy(
                        alpha = 0.1f
                    ) else Color.LightGray.copy(alpha = 0.25f)
                )
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            TableCell(
                text = "Y${yearIndex + 1}",
                weight = 1f,
                color = if (isSystemInDarkTheme()) progressOutlineDark else progressOutlineLight,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            HorizontalSpacer(8.dp)
            TableCell(
                text = screenState.calculatedFields.yearSummary[yearIndex].principalPart.toCurrency(),
                color = if (isSystemInDarkTheme()) progress1Dark else progress1Light,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            HorizontalSpacer(8.dp)
            TableCell(
                text = screenState.calculatedFields.yearSummary[yearIndex].interestPart.toCurrency(),
                style = TextStyle(fontWeight = FontWeight.Bold),
                color = if (isSystemInDarkTheme()) interestDark else interestLight,
            )
            HorizontalSpacer(8.dp)
            TableCell(
                text = screenState.calculatedFields.yearSummary[yearIndex].loanRemainder.toCurrency(),
                color = if (isSystemInDarkTheme()) progressOutlineDark else progressOutlineLight,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FinHelperTheme {
        MortgageScreen(
            screenState = MortgageScreenState(
                adsRemoved = false,

                inputFields = MortgageInputFields(
                    principalAmountInput = "500000.00",
                    interestRateInput = "2.5",
                    numberOfYearsInput = "25",
                    paymentsPerYearInput = "12",
                ),

                calculatedFields = MortgageCalculatedFields(
                    payment = "$500.00",
                    totalInterest = "$1000.00",
                    totalPayments = "$100000.00",
                    principalPercent = 50f,
                    principalAmount = "$500000.00",
                    paymentsSchedule = ScheduleItemsProvider().values.first(),
                    yearSummary = YearSummaryProvider().values.first()
                ),
            ),
            screenEvents = MortgageScreenEvents(),
            historyPanelState = HistoryPanelState(
                selectedHistoryItemIndex = 0,
                historyItemCount = 2,
            ),
            historyPanelEvents = HistoryPanelEvents()
        )
    }
}