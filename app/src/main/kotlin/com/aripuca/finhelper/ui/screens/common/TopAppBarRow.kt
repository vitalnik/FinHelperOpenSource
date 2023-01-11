package com.aripuca.finhelper.ui.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.extensions.clickableWithRipple

@Composable
fun TopAppBarRow(title: String, helpContentDescription: String, onHelpClick: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            modifier = Modifier.weight(1f, true)
        )
        Icon(
            painter = painterResource(
                id = R.drawable.ic_help
            ),
            contentDescription = helpContentDescription,
            modifier = Modifier
                .padding(16.dp)
                .clickableWithRipple {
                    onHelpClick()
                }
        )
    }
}

@Preview
@Composable
private fun TopAppBarRowPreview() {
    TopAppBarRow(title = "Title Text", helpContentDescription = "Content Description")
}