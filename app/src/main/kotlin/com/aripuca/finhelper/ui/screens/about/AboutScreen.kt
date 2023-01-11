package com.aripuca.finhelper.ui.screens.about

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.aripuca.finhelper.R
import com.aripuca.finhelper.services.billing.BillingManager
import com.aripuca.finhelper.ui.components.buttons.NavigationIcon
import com.aripuca.finhelper.ui.components.buttons.SmallButton
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer
import com.aripuca.finhelper.ui.components.text.TextLink
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutScreen(
    versionName: String,
    removeAdsPurchased: Boolean = false,
    buyMeCoffeePurchased: Boolean = false,
    productDetailsList: List<ProductDetails> = listOf(),
    onBackPress: () -> Unit = {},
    onLaunchEmail: () -> Unit = {},
    onRemoveAds: (ProductDetails) -> Unit = {},
    onBuyMeCoffee: (ProductDetails) -> Unit = {},
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    NavigationIcon {
                        onBackPress()
                    }
                }
            )
        }) { scaffoldPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, true)
                    .padding(top = scaffoldPadding.calculateTopPadding())
            ) {

                VerticalSpacer(16.dp)

                Text(
                    text = "Financial Helper",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                VerticalSpacer(8.dp)
                Text(
                    text = versionName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                VerticalSpacer()

                Text(
                    text = "Developed by Aripuca Apps",
                    style = MaterialTheme.typography.titleMedium
                )

                VerticalSpacer(8.dp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Contact us at:", style = MaterialTheme.typography.bodyMedium)
                    TextLink(text = stringResource(id = R.string.support_email)) {
                        onLaunchEmail()
                    }
                }

                VerticalSpacer()

                var supportText = ""
                if (removeAdsPurchased || buyMeCoffeePurchased) {
                    supportText =
                        "Thank you for supporting the development of the Financial Helper app. We appreciate your help."
                } else {
                    supportText =
                        "Please consider supporting the development of this app. We appreciate your help. Thanks!"
                }

                Card(
                    shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = supportText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                if (!removeAdsPurchased && !buyMeCoffeePurchased) {
                    productDetailsList.firstOrNull {
                        it.productId == BillingManager.REMOVE_ADS_INAPP_PRODUCT
                    }?.let {
                        SmallButton(text = "Remove Ads (${it.oneTimePurchaseOfferDetails?.formattedPrice})") {
                            onRemoveAds(it)
                        }
                    }
                }

                if (!buyMeCoffeePurchased) {
                    productDetailsList.firstOrNull {
                        it.productId == BillingManager.BUY_ME_COFFEE_INAPP_PRODUCT
                    }?.let {
                        VerticalSpacer()
                        SmallButton(text = "Buy Me a Coffee (${it.oneTimePurchaseOfferDetails?.formattedPrice})") {
                            onBuyMeCoffee(it)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    FinHelperTheme {
        AboutScreen("1.0.0")
    }
}