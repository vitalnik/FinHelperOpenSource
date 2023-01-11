package com.aripuca.finhelper.ui.screens.mortgage.help

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.layout.ScrollableColumn
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MortgageHelpScreen(
    onBackPress: () -> Unit = {},
) {

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.mortgage_help))
            },
            navigationIcon = {
                NavigationIcon {
                    onBackPress()
                }
            }
        )
    }) { scaffoldPadding ->

        ScrollableColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {

            VerticalSpacer(24.dp)

            Text(text = "Mortgage payments:", style = MaterialTheme.typography.headlineSmall)

            VerticalSpacer()

            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // https://latex.codecogs.com/eqneditor/editor.php
                    // {\color{White} P* \frac{r}{n} * \frac{(1+\frac{r}{n})^n^t}{(1+\frac{r}{n})^n^t-1}}
                    Image(
                        painter = painterResource(id = R.drawable.mortgage),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "Mortgage payments formula",
                        modifier = Modifier
                    )
//                    Text(
//                        text = "P × [ (r/n) × (1 + r/n)^nt ] / [ (1+r/n)^nt–1 ]",
//                        style = MaterialTheme.typography.headlineSmall
//                    )
                }
            }

            VerticalSpacer(48.dp)

            Text(text = "Where:", style = MaterialTheme.typography.titleMedium)

            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = "P - the loan amount or principal",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "t - the number of years you have to repay, the term",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "r - the annual interest rate",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "n - the number of payments per year, which would be 12 for monthly payments",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            VerticalSpacer(24.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MortgageHelpScreenPreview() {
    FinHelperTheme {
        MortgageHelpScreen()
    }
}