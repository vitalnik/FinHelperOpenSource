package com.aripuca.finhelper.ui.components.text

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.aripuca.finhelper.extensions.toCurrency
import com.aripuca.finhelper.ui.theme.principalDark
import com.aripuca.finhelper.ui.theme.principalLight

@Composable
fun AnimatedNumberText(
    value: Double,
    duration: Int = 1000,
    delay: Int = 0,
    enabled: Boolean,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    color: Color = Color.Unspecified,
    onFinished: () -> Unit = {}
) {

    var isAnimationStarted by remember { mutableStateOf(false) }

    val targetValue = when {
        isAnimationStarted -> value
        else -> 0.0
    }

    val currentValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = LinearEasing
        ),
        label = "",
        finishedListener = {
            onFinished()
        }
    )

    LaunchedEffect(key1 = value) {
        isAnimationStarted = true
    }

    Text(
        text = currentValue.toDouble().toCurrency(),
        modifier = Modifier.fillMaxWidth(),
        textAlign = textAlign,
        style = style,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = if (enabled) 1.0f else 0.2f)
    )
}
