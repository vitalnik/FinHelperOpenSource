package com.aripuca.finhelper.ui.components.buttons

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.ui.components.layout.Centered
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer

@Composable
fun CenteredTextButton(text: String, onClick: () -> Unit) {
    Centered {
        VerticalSpacer(4.dp)
        TextButton(
            onClick = onClick
        ) {
            Text(text = text)
        }
        VerticalSpacer(4.dp)
    }
}
