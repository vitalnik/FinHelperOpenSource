package com.aripuca.finhelper.ui.components.layout

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableHeaderCellFixed(text: String, width: Dp = 120.dp) {
    TableCellFixed(
        text = text,
        width = width,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
fun TableCellFixed(
    text: String,
    width: Dp = 120.dp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        style = TextStyle.Default.copy(fontWeight = fontWeight),
        textAlign = TextAlign.Center,
        color = color,
        fontSize = 13.sp
    )
}

@Composable
fun RowScope.TableHeaderCell(
    text: String,
    weight: Float = 3f,
    color: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = TextStyle(fontWeight = FontWeight.Bold)
) {
    TableCell(text = text, weight = weight, color = color, style = style)
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float = 3f,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle(fontWeight = FontWeight.Normal),
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight, true),
        color = color,
        textAlign = TextAlign.Center,
        fontSize = 13.sp,
        style = style,
    )
}
