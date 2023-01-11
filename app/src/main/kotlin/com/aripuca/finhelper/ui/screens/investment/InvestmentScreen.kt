package com.aripuca.finhelper.ui.screens.investment

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.BuildConfig
import com.aripuca.finhelper.R
import com.aripuca.finhelper.calculations.YearlyTableItem
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.ui.components.AdMobView
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.charts.InterestProgress
import com.aripuca.finhelper.ui.components.input.DropDownList
import com.aripuca.finhelper.ui.components.input.OptionItem
import com.aripuca.finhelper.ui.components.layout.*
import com.aripuca.finhelper.ui.components.text.HeaderText
import com.aripuca.finhelper.ui.screens.common.HistoryPanel
import com.aripuca.finhelper.ui.screens.common.HistoryPanelState
import com.aripuca.finhelper.ui.screens.common.TopAppBarRow
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getText
import com.aripuca.finhelper.ui.theme.*
import com.himanshoe.charty.bar.StackedBarChart
import com.himanshoe.charty.bar.config.BarConfigDefaults
import com.himanshoe.charty.bar.model.StackedBarData
import com.himanshoe.charty.common.axis.AxisConfigDefaults


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InvestmentScreen(
    adsRemoved: Boolean,
    inputValid: Boolean,
    totalValue: String,
    totalInterestEarned: String,
    totalInvestment: String,
    principalPercent: Float,
    principalAmount: String,
    onPrincipalAmountChanged: (String) -> Unit = {},
    regularAddition: String,
    onRegularAdditionChanged: (String) -> Unit = {},
    interestRate: String,
    onInterestRateChanged: (String) -> Unit = {},
    yearsToGrow: String,
    onYearsToGrowChanged: (String) -> Unit = {},
    regularAdditionFrequency: Frequency,
    onRegularAdditionFrequencyChanged: (Frequency) -> Unit = {},
    yearlyTable: List<YearlyTableItem> = listOf(),
    onHelpClick: () -> Unit = {},
    onBackPress: () -> Unit = {},
    historyPanelState: HistoryPanelState = HistoryPanelState(),
) {

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarRow(
                        title = stringResource(id = R.string.investment_calculator),
                        helpContentDescription = stringResource(R.string.investment_help),
                        onHelpClick = onHelpClick
                    )
                },
                navigationIcon = {
                    NavigationIcon {
                        onBackPress()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { scaffoldPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {

            val horScrollState = rememberScrollState()
            LazyColumn(state = listState, modifier = Modifier.weight(1f, true)) {

                item {
                    InputFields(
                        principalAmount,
                        onPrincipalAmountChanged,
                        interestRate,
                        onInterestRateChanged,
                        yearsToGrow,
                        onYearsToGrowChanged,
                        regularAddition,
                        onRegularAdditionChanged,
                        regularAdditionFrequency,
                        onRegularAdditionFrequencyChanged
                    )
                }

                item {
                    HistoryPanel(
                        selectedHistoryItemIndex = historyPanelState.selectedHistoryItemIndex,
                        historyItemCount = historyPanelState.historyItemCount,
                        saveEnabled = historyPanelState.saveHistoryItemEnabled,
                        onAddClick = historyPanelState.onAddHistoryItem,
                        onSaveClick = historyPanelState.onSaveHistoryItem,
                        onDeleteClick = historyPanelState.onDeleteHistoryItem,
                        onPrevClick = historyPanelState.onLoadPrevHistoryItem,
                        onNextClick = historyPanelState.onLoadNextHistoryItem,
                    )
                }

                item {
                    AnimatedVisibility(visible = inputValid) {
                        InvestmentChart(yearlyTable)
                    }
                }

                stickyHeader {
                    CalculatedFields(
                        totalInvestment,
                        totalInterestEarned,
                        totalValue,
                        principalPercent
                    )
                    if (inputValid) {
                        Surface {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    HeaderText(text = stringResource(id = R.string.yearly_table))
                                }
                                YearlyTableHeader(horScrollState)
                            }
                        }
                    }
                }

                if (inputValid) {
                    itemsIndexed(items = yearlyTable) { index, item ->

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
                }
            }

            if (!adsRemoved && !BuildConfig.DEBUG) {
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

    if (stackBarData.isEmpty()) {
        return
    }

    Column {

        VerticalSpacer()

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = stringResource(id = R.string.interest),
                modifier = Modifier,
                color = if (isSystemInDarkTheme()) interestDark else interestLight,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
        }

        VerticalSpacer(8.dp)

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

@Composable
private fun CalculatedFields(
    totalInvestment: String,
    totalInterestEarned: String,
    totalValue: String,
    principalPercent: Float
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
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            LabelRow(
                label = stringResource(id = R.string.total_interest),
                value = totalInterestEarned,
                color = if (isSystemInDarkTheme()) interestDark else interestLight
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            LabelRow(
                label = stringResource(id = R.string.total_value),
                value = totalValue,
                color = if (isSystemInDarkTheme()) totalDark else totalLight
            )

            VerticalSpacer()
        }
    }
}

@Composable
private fun InputFields(
    principalAmount: String,
    onPrincipalAmountChanged: (String) -> Unit,
    interestRate: String,
    onInterestRateChanged: (String) -> Unit,
    yearsToGrow: String,
    onYearsToGrowChanged: (String) -> Unit,
    regularAddition: String,
    onRegularAdditionChanged: (String) -> Unit,
    regularAdditionFrequency: Frequency,
    onRegularAdditionFrequencyChanged: (Frequency) -> Unit
) {

    VerticalSpacer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextFieldRow(
            label = stringResource(id = R.string.initial_investment),
            value = principalAmount,
            onValueChanged = onPrincipalAmountChanged,
        )
        HorizontalSpacer()
        TextFieldRow(
            label = stringResource(R.string.interest_rate),
            value = interestRate,
            onValueChanged = onInterestRateChanged,
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
            value = yearsToGrow,
            onValueChanged = onYearsToGrowChanged,
        )
        HorizontalSpacer()
        TextFieldRow(
            label = stringResource(id = R.string.regular_contribution),
            value = regularAddition,
            onValueChanged = onRegularAdditionChanged,
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
                regularAdditionFrequency.getText(),
                regularAdditionFrequency
            ),
            options = optionItems
        ) {
            onRegularAdditionFrequencyChanged(it.value)
        }
    }
}

@Composable
private fun RowScope.TextFieldRow(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = Modifier.weight(1f, true),
        label = { Text(text = label) },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        singleLine = true,
    )
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
        TableHeaderCellFixed(stringResource(R.string.total_interest))
        TableHeaderCellFixed(stringResource(R.string.total_value))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinHelperTheme {
        InvestmentScreen(
            adsRemoved = false,
            inputValid = true,
            totalValue = "",
            totalInterestEarned = "",
            totalInvestment = "",
            principalPercent = 50f,
            principalAmount = "",
            regularAddition = "",
            interestRate = "",
            yearsToGrow = "",
            regularAdditionFrequency = Frequency.MONTHLY,
            yearlyTable = listOf(),
        )
    }
}

