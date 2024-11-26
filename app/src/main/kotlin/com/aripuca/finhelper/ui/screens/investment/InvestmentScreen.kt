package com.aripuca.finhelper.ui.screens.investment

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.BuildConfig
import com.aripuca.finhelper.R
import com.aripuca.finhelper.calculations.YearlyTableItem
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.ui.components.AdMobView
import com.aripuca.finhelper.ui.components.buttons.CenteredButton
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.charts.InterestProgress
import com.aripuca.finhelper.ui.components.input.DropDownList
import com.aripuca.finhelper.ui.components.input.OptionItem
import com.aripuca.finhelper.ui.components.input.TextFieldRow
import com.aripuca.finhelper.ui.components.layout.HorizontalSpacer
import com.aripuca.finhelper.ui.components.layout.LabelRow
import com.aripuca.finhelper.ui.components.layout.TableCellFixed
import com.aripuca.finhelper.ui.components.layout.TableHeaderCellFixed
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.components.text.HeaderText
import com.aripuca.finhelper.ui.screens.common.HistoryPanel
import com.aripuca.finhelper.ui.screens.common.HistoryPanelEvents
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.common.TopAppBarRow
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getFrequency
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getText
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.aripuca.finhelper.ui.theme.bottomSheetPickHeight
import com.aripuca.finhelper.ui.theme.interestDark
import com.aripuca.finhelper.ui.theme.interestLight
import com.aripuca.finhelper.ui.theme.principalDark
import com.aripuca.finhelper.ui.theme.principalLight
import com.aripuca.finhelper.ui.theme.totalDark
import com.aripuca.finhelper.ui.theme.totalLight
import com.himanshoe.charty.bar.StackedBarChart
import com.himanshoe.charty.bar.config.BarConfigDefaults
import com.himanshoe.charty.bar.model.StackedBarData
import com.himanshoe.charty.common.axis.AxisConfigDefaults
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InvestmentScreen(
    screenState: InvestmentScreenState,
    screenEvents: InvestmentScreenEvents,
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
                        title = stringResource(id = R.string.investment_calculator),
                        helpContentDescription = stringResource(R.string.investment_help),
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
                YearlyTable(screenState.calculatedFields.yearlyTable)
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

            LazyColumn(state = listState, modifier = Modifier.weight(1f, true)) {

                item {
                    VerticalSpacer()
                    InputFields(
                        state = screenState.inputFields,
                        events = screenEvents.toInputFieldEvents()
                    )
                }

                item {
                    VerticalSpacer(height = 24.dp)
                    HistoryPanel(
                        state = historyPanelState,
                        events = historyPanelEvents
                    )
                }

                item {
                    InvestmentChart(screenState.calculatedFields.yearlyTable)
                }

                item {
                    with(screenState.calculatedFields) {
                        CalculatedFields(
                            totalInvestment,
                            totalInterestEarned,
                            totalValue,
                            principalPercent
                        )
                    }

                    if (!scaffoldState.bottomSheetState.isVisible) {
                        VerticalSpacer()
                        CenteredButton(text = stringResource(id = R.string.yearly_table)) {
                            screenEvents.onYearlyTableClicked()
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
                AdMobView(adUnitId = stringResource(id = R.string.investment_screen_banner_ad_unit_id))
            }
        }
    }
}

@Composable
private fun InvestmentChart(yearlyTable: List<YearlyTableItem>) {

    val stackBarData = mutableListOf<StackedBarData>()

    yearlyTable.forEachIndexed { index, item ->
        val yValue = mutableListOf<Float>()
        yValue.add(item.totalInvestment.toFloat())
        yValue.add(item.totalInterest.toFloat())

        stackBarData.add(StackedBarData(xValue = index + 1, yValue = yValue))
    }

    Column {

        VerticalSpacer()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("investment_chart_title"),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.investment),
                modifier = Modifier,
                color = if (isSystemInDarkTheme()) principalDark else principalLight,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(id = R.string.and),
                modifier = Modifier.padding(horizontal = 8.dp),
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(id = R.string.earned_interest),
                modifier = Modifier,
                color = if (isSystemInDarkTheme()) interestDark else interestLight,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
        }

        VerticalSpacer(8.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center
        ) {
            if (stackBarData.isNotEmpty()) {
                StackedBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
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
            } else {
                Text(
                    text = stringResource(id = R.string.not_enough_data),
                    color = if (isSystemInDarkTheme()) principalDark else principalLight,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }
    }
}

@Composable
private fun CalculatedFields(
    totalInvestment: String,
    totalInterestEarned: String,
    totalValue: String,
    principalPercent: Double
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            VerticalSpacer()

            InterestProgress(principalPercent)

            VerticalSpacer()

            LabelRow(
                label = stringResource(id = R.string.total_investment),
                value = totalInvestment,
                color = if (isSystemInDarkTheme()) principalDark else principalLight
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            LabelRow(
                label = stringResource(id = R.string.interest_earned),
                value = totalInterestEarned,
                color = if (isSystemInDarkTheme()) interestDark else interestLight
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            LabelRow(
                label = stringResource(id = R.string.total_value),
                value = totalValue,
                color = if (isSystemInDarkTheme()) totalDark else totalLight
            )
        }
    }
}

@Composable
private fun InputFields(
    state: InvestmentInputFields,
    events: InputFieldsEvents,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextFieldRow(
            label = stringResource(id = R.string.initial_investment),
            value = state.initialInvestmentInput,
            onValueChanged = events.onPrincipalAmountChanged,
        )
        HorizontalSpacer()
        TextFieldRow(
            label = stringResource(R.string.interest_rate),
            value = state.interestRateInput,
            onValueChanged = events.onInterestRateChanged,
        )
    }

    VerticalSpacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextFieldRow(
            label = stringResource(id = R.string.number_of_years),
            value = state.yearsToGrowInput,
            onValueChanged = events.onYearsToGrowChanged,
        )
        HorizontalSpacer()
        TextFieldRow(
            label = stringResource(id = R.string.regular_contribution),
            value = state.regularContributionInput,
            onValueChanged = events.onRegularAdditionChanged,
        )
    }

    VerticalSpacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        val optionItems = listOf(
            OptionItem(Frequency.WEEKLY.getText(), Frequency.WEEKLY),
            OptionItem(Frequency.MONTHLY.getText(), Frequency.MONTHLY),
            OptionItem(Frequency.QUARTERLY.getText(), Frequency.QUARTERLY),
            OptionItem(Frequency.ANNUALLY.getText(), Frequency.ANNUALLY),
        )

        DropDownList(
            label = stringResource(id = R.string.contribution_and_compounding_frequency),
            selectedOption = OptionItem(
                state.regularAdditionFrequencyInput.getFrequency().getText(),
                state.regularAdditionFrequencyInput.getFrequency()
            ),
            options = optionItems
        ) {
            events.onRegularAdditionFrequencyChanged(it.value)
        }
    }
}

@Composable
private fun YearlyTableHeader(scrollState: ScrollState) {
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSystemInDarkTheme()) 0.25f else 0.5f))
            .padding(vertical = 8.dp)
            .drawBehind {
                val y = size.height + 8.dp.toPx()
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableHeaderCellFixed("#", width = 50.dp)
        TableHeaderCellFixed(stringResource(R.string.yearly_investment))
        TableHeaderCellFixed(stringResource(R.string.total_investment))
        TableHeaderCellFixed(stringResource(R.string.yearly_interest))
        TableHeaderCellFixed(stringResource(R.string.interest_earned))
        TableHeaderCellFixed(stringResource(R.string.total_value))
    }
}

@Composable
private fun YearlyTable(
    yearlyTable: List<YearlyTableItem>
) {
    val horScrollState = rememberScrollState()

    HeaderText(text = stringResource(R.string.yearly_table))
    YearlyTableHeader(horScrollState)

    LazyColumn(
        state = rememberLazyListState(),
    ) {

        itemsIndexed(items = yearlyTable, key = { index, _ ->
            index
        }) { index, item ->

            var backgroundColor = Color.Transparent
            if ((index + 1) % 2 == 0) {
                backgroundColor = if (isSystemInDarkTheme()) Color.LightGray.copy(
                    alpha = 0.1f
                ) else Color.LightGray.copy(alpha = 0.25f)
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(horScrollState)
                    .padding(horizontal = 16.dp)
                    .background(color = backgroundColor)
                    .padding(vertical = 4.dp)
            ) {
                TableCellFixed(text = (index + 1).toString(), width = 50.dp)
                TableCellFixed(text = item.yearlyInvestment.toCurrency())
                TableCellFixed(text = item.totalInvestment.toCurrency())
                TableCellFixed(text = item.yearlyInterest.toCurrency())
                TableCellFixed(text = item.totalInterest.toCurrency())
                TableCellFixed(text = item.totalValue.toCurrency())
            }
        }
        item {
            VerticalSpacer(height = 32.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinHelperTheme {
        InvestmentScreen(
            screenState = InvestmentScreenState(
                inputFields = InvestmentInputFields(),
                calculatedFields = InvestmentCalculatedFields(),
                adsRemoved = false,
            ),
            screenEvents = InvestmentScreenEvents(),
            historyPanelState = HistoryPanelState(),
            historyPanelEvents = HistoryPanelEvents()
        )
    }
}

