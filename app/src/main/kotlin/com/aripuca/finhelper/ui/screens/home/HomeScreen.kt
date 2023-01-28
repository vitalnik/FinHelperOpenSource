package com.aripuca.finhelper.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.ui.components.buttons.IconButton
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.components.text.TextLink
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun HomeScreen(
    versionName: String,
    navigateToMortgage: () -> Unit = {},
    navigateToInvestment: () -> Unit = {},
    navigateToMy1stMillion: () -> Unit = {},
    navigateToAbout: () -> Unit = {},
) {

    Box(contentAlignment = Alignment.BottomCenter) {

        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.FillWidth,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.weight(3f, true),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.logo
                    ),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                TextLink(text = versionName, testTag = "version_link") {
                    navigateToAbout()
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f, true),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IconButton(text = stringResource(R.string.mortgage), iconId = R.drawable.ic_house) {
                    navigateToMortgage()
                }

                VerticalSpacer(24.dp)

                IconButton(
                    text = stringResource(R.string.investment),
                    iconId = R.drawable.ic_savings
                ) {
                    navigateToInvestment()
                }

                VerticalSpacer(24.dp)

                IconButton(
                    text = "My 1st Million",
                    //textColor = if (isSystemInDarkTheme()) goldDark else goldLight,
                    iconId = R.drawable.ic_diamond
                ) {
                    navigateToMy1stMillion()
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinHelperTheme {
        HomeScreen("Ver. 1.0.0")
    }
}