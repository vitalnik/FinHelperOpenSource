package com.aripuca.finhelper.ui.screens.investment.help

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InvestmentHelpScreen(
    onBackPress: () -> Unit = {},
) {

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.investment_help))
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

            Text(
                text = "Compound interest for principal:",
                style = MaterialTheme.typography.headlineSmall
            )
            VerticalSpacer()
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    // https://latex.codecogs.com/eqneditor/editor.php
                    // {\color{White} P * (1 + \frac{r}{n})^n * t}
                    Image(
                        painter = painterResource(id = R.drawable.compound),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "Compound interest for principal formula",
                        modifier = Modifier
                    )
                }
            }
            VerticalSpacer(24.dp)
            Text(text = "Future value of a series:", style = MaterialTheme.typography.headlineSmall)
            VerticalSpacer()
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    // https://latex.codecogs.com/eqneditor/editor.php
                    // {\color{White}  PC * \frac{p}{n} *  \frac{(1 + \frac{r}{n})^n^t - 1} { \frac{r}{n} } }
                    Image(
                        painter = painterResource(id = R.drawable.future_value),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "Future value of a series formula",
                        modifier = Modifier
                    )
//                    Text(
//                        text = "MP × p/n × {[(1 + r/n)^nt - 1] / (r/n)}",
//                        style = MaterialTheme.typography.headlineSmall
                }
            }
            VerticalSpacer(48.dp)

            Text(text = "Where:", style = MaterialTheme.typography.titleMedium)

            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = "P - the initial investment amount",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "p - the number of periodic payments in the compounding period",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "PC - the periodic contribution amount",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "r - the annual interest rate",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "n - the number of times that interest is compounded per unit t",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8.dp)
                Text(
                    text = "t - the time (months, years, etc) the money is invested",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            VerticalSpacer(24.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvestmentHelpScreenPreview() {
    FinHelperTheme {
        InvestmentHelpScreen()
    }
}