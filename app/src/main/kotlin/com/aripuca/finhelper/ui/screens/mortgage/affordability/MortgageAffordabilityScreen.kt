package com.aripuca.finhelper.ui.screens.mortgage.affordability

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.input.OutlinedScrollableTabRow
import com.aripuca.finhelper.ui.components.input.TextFieldRow
import com.aripuca.finhelper.ui.components.layout.Centered
import com.aripuca.finhelper.ui.components.layout.HorizontalSpacer
import com.aripuca.finhelper.ui.components.layout.ScrollableColumn
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.components.text.AnimatedNumberText
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.aripuca.finhelper.ui.theme.LocalDimensions
import com.aripuca.finhelper.ui.theme.loanAmountDark
import com.aripuca.finhelper.ui.theme.loanAmountLight
import com.aripuca.finhelper.ui.theme.monthlyPaymentDark
import com.aripuca.finhelper.ui.theme.monthlyPaymentLight
import com.aripuca.finhelper.ui.theme.principalDark
import com.aripuca.finhelper.ui.theme.principalLight

@Composable
fun MortgageAffordabilityScreen(
    monthlyIncome: String,
    monthlyDebtPayment: String,
    downPayment: String,
    interestRate: String,
    selectedYearIndex: Int,
    yearsList: List<String>,
    calculatedLoanAmount: Double,
    calculatedPurchasePrice: Double,
    calculatedMonthlyPayment: Double,
    canCalculate: Boolean,
    onMonthlyIncomeChanged: (String) -> Unit,
    onMonthlyDebtPaymentChanged: (String) -> Unit,
    onDownPaymentChanged: (String) -> Unit,
    onInterestRateChanged: (String) -> Unit,
    onYearSelected: (Int) -> Unit,
    onCalculateClicked: () -> Unit = {},
    onOpenInMortgageClicked: () -> Unit = {},
    onBackPress: () -> Unit = {},
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.mortgage_affordability_calculator))
            },
            navigationIcon = {
                NavigationIcon {
                    onBackPress()
                }
            },
            scrollBehavior = scrollBehavior
        )
    }) { scaffoldPadding ->

        var animationFinished by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {

            ScrollableColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true)
            ) {

                VerticalSpacer()
                InputFields(
                    monthlyIncome = monthlyIncome,
                    monthlyDebtPayment = monthlyDebtPayment,
                    downPayment = downPayment,
                    interestRate = interestRate,
                    onMonthlyIncomeChanged = onMonthlyIncomeChanged,
                    onMonthlyDebtPaymentChanged = onMonthlyDebtPaymentChanged,
                    onDownPaymentChanged = onDownPaymentChanged,
                    onInterestRateChanged = onInterestRateChanged,
                )

                VerticalSpacer(12.dp)

                OutlinedScrollableTabRow(
                    label = stringResource(id = R.string.amortization_period),
                    selectedTabIndex = selectedYearIndex,
                    tabsList = yearsList,
                    onTabSelected = onYearSelected
                )

                VerticalSpacer(8.dp)

                Centered {

                    Button(
                        onClick = {
                            animationFinished = false
                            onCalculateClicked()
                        },
                        enabled = canCalculate,
                        modifier = Modifier.width(width = 200.dp)
                    ) {
                        Text(text = stringResource(id = R.string.calculate))
                    }

                    LoanDetailsCard(
                        animationFinished = animationFinished,
                        calculatedLoanAmount = calculatedLoanAmount,
                        calculatedMonthlyPayment = calculatedMonthlyPayment,
                        enabled = !canCalculate
                    ) {
                        animationFinished = true
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = LocalDimensions.current.default),
                        elevation = CardDefaults.cardElevation(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        PurchasePrice(
                            calculatedPurchasePrice = calculatedPurchasePrice,
                            enabled = !canCalculate
                        ) {
                            //animationFinished = true
                        }
                    }

                    VerticalSpacer()

                    Button(
                        onClick = {
                            onOpenInMortgageClicked()
                        },
                        enabled = !canCalculate,
                    ) {
                        Text(text = stringResource(id = R.string.open_in_mortgage_calculator))
                    }

                    VerticalSpacer()
                }
            }
        }
    }
}

@Composable
private fun LoanDetailsCard(
    animationFinished: Boolean,
    calculatedLoanAmount: Double,
    calculatedMonthlyPayment: Double,
    enabled: Boolean,
    onAnimationFinished: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalDimensions.current.default),
        elevation = CardDefaults.cardElevation(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.height(180.dp)) {
            HouseAnimation(
                animationFinished = animationFinished,
                enabled = enabled
            )
            LoanDetails(
                calculatedLoanAmount = calculatedLoanAmount,
                calculatedMonthlyPayment = calculatedMonthlyPayment,
                enabled = enabled
            ) {
                onAnimationFinished()
            }
        }
    }

}

@Composable
private fun PurchasePrice(
    calculatedPurchasePrice: Double,
    enabled: Boolean = true,
    onLoanAmountAnimationFinished: () -> Unit,
) {
    Column(Modifier.padding(LocalDimensions.current.default)) {
        Text(
            text = stringResource(id = R.string.purchase_price),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.2f)
        )
        VerticalSpacer()
        AnimatedNumberText(
            value = calculatedPurchasePrice,
            enabled = enabled,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
            color = if (isSystemInDarkTheme()) principalDark else principalLight,
            duration = 1000,
            delay = 1000,
            onFinished = {
                onLoanAmountAnimationFinished()
            }
        )
    }
}

@Composable
private fun RowScope.HouseAnimation(
    animationFinished: Boolean,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f, true),
        contentAlignment = Alignment.Center
    ) {

        val iconSize = remember {
            150.dp
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_house),
            modifier = Modifier.size(size = iconSize),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            contentDescription = "House"
        )

        this@HouseAnimation.AnimatedVisibility(
            visible = animationFinished && enabled,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 0,
                    easing = LinearEasing
                )
            ),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_house),
                modifier = Modifier.size(size = iconSize),
                tint = if (isSystemInDarkTheme()) principalDark else principalLight,
                contentDescription = "House"
            )
        }
    }

}

@Composable
private fun RowScope.LoanDetails(
    calculatedLoanAmount: Double,
    calculatedMonthlyPayment: Double,
    enabled: Boolean,
    onAnimationFinished: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f, true),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.loan_amount),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.2f)
        )

        AnimatedNumberText(
            value = calculatedLoanAmount,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSystemInDarkTheme()) loanAmountDark else loanAmountLight,
            duration = 1000,
            delay = 0,
            enabled = enabled,
        )

        VerticalSpacer()

        Text(
            text = stringResource(id = R.string.monthly_payment),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.2f)
        )
        AnimatedNumberText(
            value = calculatedMonthlyPayment,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSystemInDarkTheme()) monthlyPaymentDark else monthlyPaymentLight,
            duration = 1000,
            delay = 500,
            onFinished = {
                onAnimationFinished()
            },
            enabled = enabled
        )
    }

}

@Composable
private fun InputFields(
    monthlyIncome: String,
    monthlyDebtPayment: String,
    downPayment: String,
    interestRate: String,
    onMonthlyIncomeChanged: (String) -> Unit,
    onMonthlyDebtPaymentChanged: (String) -> Unit,
    onDownPaymentChanged: (String) -> Unit,
    onInterestRateChanged: (String) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextFieldRow(
                label = stringResource(id = R.string.monthly_income),
                value = monthlyIncome,
                onValueChanged = onMonthlyIncomeChanged,
            )
            HorizontalSpacer()
            TextFieldRow(
                label = stringResource(R.string.monthly_debt_payment),
                value = monthlyDebtPayment,
                onValueChanged = onMonthlyDebtPaymentChanged,
            )
        }
        VerticalSpacer()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextFieldRow(
                label = stringResource(id = R.string.down_payment),
                value = downPayment,
                onValueChanged = onDownPaymentChanged,
            )
            HorizontalSpacer()
            TextFieldRow(
                label = stringResource(R.string.expected_interest_rate),
                value = interestRate,
                onValueChanged = onInterestRateChanged,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MortgageAffordabilityScreen_Preview() {
    FinHelperTheme {
        MortgageAffordabilityScreen(
            monthlyIncome = "10000.00",
            monthlyDebtPayment = "500.00",
            downPayment = "50000.00",
            interestRate = "5.0",
            selectedYearIndex = 0,
            yearsList = listOf("15 years", "20 years", "25 years"),
            calculatedLoanAmount = 500000.0,
            calculatedPurchasePrice = 550000.0,
            calculatedMonthlyPayment = 1000.0,
            canCalculate = false,
            onMonthlyIncomeChanged = {},
            onMonthlyDebtPaymentChanged = {},
            onDownPaymentChanged = {},
            onInterestRateChanged = {},
            onYearSelected = {
            },
            onCalculateClicked = {},
            onOpenInMortgageClicked = {},
            onBackPress = {}
        )
    }
}
