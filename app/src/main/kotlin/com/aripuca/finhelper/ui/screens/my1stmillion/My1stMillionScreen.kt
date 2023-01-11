package com.aripuca.finhelper.ui.screens.my1stmillion

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.aripuca.finhelper.extensions.fromCurrencyToDouble
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.ui.components.AdMobView
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.input.DropDownList
import com.aripuca.finhelper.ui.components.input.OptionItem
import com.aripuca.finhelper.ui.components.layout.*
import com.aripuca.finhelper.ui.screens.investment.Frequency
import com.aripuca.finhelper.ui.screens.investment.Frequency.Companion.getText2
import com.aripuca.finhelper.ui.theme.*
import com.himanshoe.charty.pie.PieChart
import com.himanshoe.charty.pie.config.PieConfig
import com.himanshoe.charty.pie.config.PieData
import java.util.*
import com.aripuca.finhelper.ui.components.buttons.IconButton
import com.aripuca.finhelper.ui.components.text.TextLink

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun My1stMillionScreen(
    adsRemoved: Boolean,
    totalValue: String,
    totalInterestEarned: String,
    totalInvestment: String,
    initialPrincipalAmount: String,
    yearsToGrow: String,
    onInitialPrincipalAmountChanged: (String) -> Unit = {},
    regularAddition: String,
    onRegularAdditionChanged: (String) -> Unit = {},
    interestRate: String,
    onInterestRateChanged: (String) -> Unit = {},
    regularAdditionFrequency: Frequency,
    onRegularAdditionFrequencyChanged: (Frequency) -> Unit = {},
    onOpenInInvestments: () -> Unit = {},
    onBackPress: () -> Unit = {},
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "My 1st Million")
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
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {

            ScrollableColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true)
            ) {

                InputFields(
                    initialPrincipalAmount = initialPrincipalAmount,
                    onInitialPrincipalAmountChanged = onInitialPrincipalAmountChanged,
                    interestRate = interestRate,
                    onInterestRateChanged = onInterestRateChanged,
                    regularAddition = regularAddition,
                    onRegularAdditionChanged = onRegularAdditionChanged,
                    regularAdditionFrequency = regularAdditionFrequency,
                    onRegularAdditionFrequencyChanged = onRegularAdditionFrequencyChanged
                )

                GoalPanel(
                    yearsToGrow = yearsToGrow.toIntOrNull() ?: 0
                )

                GoalChart(
                    totalInvestment = totalInvestment.fromCurrencyToDouble(),
                    totalInterestEarned = totalInterestEarned.fromCurrencyToDouble(),
                    yearsToGrow = yearsToGrow
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    TextLink(text = "Open in Investments", fontSize = 18.sp) {
                        onOpenInInvestments()
                    }
//                    IconButton(
//                        text = "Open in Investments",
//                        iconId = R.drawable.ic_diamond
//                    ) {
//                        onOpenInInvestments()
//                    }
                }
            }

            if (!adsRemoved && !BuildConfig.DEBUG) {
                AdMobView(adUnitId = stringResource(id = R.string.my1st_million_screen_banner_ad_unit_id))
            }
        }
    }
}

@Composable
private fun GoalChart(totalInvestment: Double, totalInterestEarned: Double, yearsToGrow: String) {

    val pieDataList = mutableListOf<PieData>()
    pieDataList.add(
        PieData(
            data = totalInvestment.toFloat(),
            color = if (isSystemInDarkTheme()) principalDark else principalLight
        )
    )
    pieDataList.add(
        PieData(
            data = totalInterestEarned.toFloat(),
            color = if (isSystemInDarkTheme()) interestDark else interestLight
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .width(320.dp)
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {

            PieChart(
                pieData = pieDataList,
                modifier = Modifier
                    .width(240.dp)
                    .height(240.dp),
                config = PieConfig(isDonut = true)
            )

            val arcColor = if (isSystemInDarkTheme()) totalDark else totalLight
            Canvas(
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            ) {
                drawArc(
                    color = arcColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = 20f
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (yearsToGrow == "100") ">100" else yearsToGrow,
                    style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold),
                    color = if (isSystemInDarkTheme()) totalDark else totalLight
                )
                Text(
                    text = "year(s)",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
        ) {

            LabelColumn(
                label = stringResource(id = R.string.total_investment),
                value = totalInvestment.toCurrency(),
                color = if (isSystemInDarkTheme()) principalDark else principalLight
            )

            HorizontalSpacer()

            LabelColumn(
                label = stringResource(id = R.string.total_interest),
                value = totalInterestEarned.toCurrency(),
                color = if (isSystemInDarkTheme()) interestDark else interestLight,
                horizontalAlignment = Alignment.End
            )
        }
    }
}

@Composable
private fun GoalPanel(
    yearsToGrow: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        VerticalSpacer(24.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My goal of",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
            VerticalSpacer(8.dp)
            Text(
                text = "$ 1,000,000.00",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) totalDark else totalLight,
                    fontSize = 32.sp
                )
            )
            VerticalSpacer(8.dp)
            Text(
                text = "will become a reality in${if (yearsToGrow <= 10) " just" else ""}",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
private fun InputFields(
    initialPrincipalAmount: String,
    onInitialPrincipalAmountChanged: (String) -> Unit,
    interestRate: String,
    onInterestRateChanged: (String) -> Unit,
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
            value = initialPrincipalAmount,
            onValueChanged = onInitialPrincipalAmountChanged,
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
            label = stringResource(id = R.string.regular_contribution),
            value = regularAddition,
            onValueChanged = onRegularAdditionChanged,
        )
        HorizontalSpacer()

        val optionItems = listOf(
            OptionItem(Frequency.WEEKLY.getText2(), Frequency.WEEKLY),
            OptionItem(Frequency.MONTHLY.getText2(), Frequency.MONTHLY),
            OptionItem(Frequency.QUARTERLY.getText2(), Frequency.QUARTERLY),
            OptionItem(Frequency.ANNUALLY.getText2(), Frequency.ANNUALLY),
        )

        Row(modifier = Modifier.weight(weight = 1f, fill = true)) {
            DropDownList(
                label = "Contribution frequency",
                selectedOption = OptionItem(
                    regularAdditionFrequency.getText2(),
                    regularAdditionFrequency
                ),
                options = optionItems
            ) {
                onRegularAdditionFrequencyChanged(it.value)
            }
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinHelperTheme {
        My1stMillionScreen(
            adsRemoved = false,
            totalValue = "$ 1,000,000.00",
            totalInterestEarned = "$ 250,000.00",
            totalInvestment = "$ 750,000.00",
            initialPrincipalAmount = "$ 1,000.00",
            yearsToGrow = "25",
            regularAddition = "$ 1,000.00",
            interestRate = "3.5",
            regularAdditionFrequency = Frequency.MONTHLY,
        )
    }
}

