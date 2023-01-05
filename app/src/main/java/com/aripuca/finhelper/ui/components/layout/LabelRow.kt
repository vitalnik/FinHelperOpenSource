package com.aripuca.finhelper.ui.components.layout

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun LabelRow(label: String, value: String, color: Color = Color.Unspecified, fontSize: TextUnit = 16.sp) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f, true),
            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = fontSize)
        )
        HorizontalSpacer()
        Text(
            text = value,
            modifier = Modifier
                .weight(1f, true),
            style = TextStyle(fontWeight = FontWeight.Bold, color = color, fontSize = fontSize)
        )
    }
}