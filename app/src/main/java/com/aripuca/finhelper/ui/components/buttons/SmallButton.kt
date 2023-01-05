package com.aripuca.finhelper.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun SmallButton(text: String, onClick: () -> Unit = {}) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(50),
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
            style = TextStyle(fontSize = 14.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SmallButtonPreview() {
    FinHelperTheme {
        SmallButton(text = "Button Title")
    }
}