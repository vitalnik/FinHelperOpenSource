package com.aripuca.finhelper.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.extensions.clickableWithRipple

@Composable
fun NavigationIcon(onClick: () -> Unit = {}) {
    Icon(
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = "Back",
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .clickableWithRipple {
                onClick()
            }
    )
}

