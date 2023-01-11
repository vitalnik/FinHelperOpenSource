package com.aripuca.finhelper.ui.components.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.LabelColumn(
    label: String, value: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, true),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = label,
            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = fontSize)
        )
        Text(
            text = value,
            style = TextStyle(fontWeight = FontWeight.Bold, color = color, fontSize = 20.sp)
        )
    }
}