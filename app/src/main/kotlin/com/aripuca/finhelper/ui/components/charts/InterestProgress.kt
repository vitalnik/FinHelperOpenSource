package com.aripuca.finhelper.ui.components.charts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.ui.theme.*

@Composable
fun InterestProgress(principalPercent: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isSystemInDarkTheme()) progressOutlineDark else progressOutlineLight
                ),
                shape = RoundedCornerShape(50)
            )
            .clip(
                shape = RoundedCornerShape(50)
            )

    ) {
        if (principalPercent > 0f) {
            Box(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            topStart = 8.dp,
                            bottomStart = 8.dp
                        ), color = if (isSystemInDarkTheme()) progress1Dark else progress1Light
                    )
                    .height(24.dp)
                    .weight(principalPercent.toFloat(), true)
            )
        }
        if (principalPercent < 100f) {
            Box(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            topStart = 0.dp,
                            bottomStart = 0.dp
                        ),
                        color = if (isSystemInDarkTheme()) progress2Dark else progress2Light
                    )
                    .height(24.dp)
                    .weight(100f - principalPercent.toFloat(), true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterestProgressPreview() {
    FinHelperTheme {
        InterestProgress(50.0)
    }
}
