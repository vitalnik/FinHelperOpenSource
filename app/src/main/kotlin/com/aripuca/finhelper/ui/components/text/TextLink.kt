package com.aripuca.finhelper.ui.components.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextLink(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    testTag: String = "text_link",
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(4.dp)
            .testTag(testTag),
        style = TextStyle(fontSize = fontSize, fontWeight = FontWeight.Bold),
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary
    )
}
