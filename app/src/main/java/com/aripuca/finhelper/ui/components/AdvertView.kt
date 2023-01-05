package com.aripuca.finhelper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobView(modifier: Modifier = Modifier, adUnitId: String = "") {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onTertiary,
            text = "AdMob Ad",
        )
    } else {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AdMobViewPreview() {
    AdMobView()
}