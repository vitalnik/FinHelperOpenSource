package com.aripuca.finhelper.ui.components.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.aripuca.finhelper.ui.components.layout.Centered

@Composable
fun CenteredButton(text: String, onClick: () -> Unit) {
    Centered {
        Button(onClick = onClick) {
            Text(text = text)
        }
    }
}
