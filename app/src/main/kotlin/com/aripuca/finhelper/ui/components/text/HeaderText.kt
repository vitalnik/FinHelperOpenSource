package com.aripuca.finhelper.ui.components.text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ,
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Composable
private fun HeaderTextPreview(text: String) {
    FinHelperTheme {
        HeaderText(text = "Header Text")
    }
}