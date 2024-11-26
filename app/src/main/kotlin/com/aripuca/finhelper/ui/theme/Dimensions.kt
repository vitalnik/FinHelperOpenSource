package com.aripuca.finhelper.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val bottomSheetPickHeight = 130.dp

val LocalDimensions =
    staticCompositionLocalOf<Dimensions> { error("No local dimensions found!") }

interface Dimensions {
    val xSmall: Dp
    val small: Dp
    val medium: Dp
    val default: Dp
    val large: Dp
    val xLarge: Dp
}

data class CompactDimensions(
    override val xSmall: Dp = 2.dp,
    override val small: Dp = 4.dp,
    override val medium: Dp = 8.dp,
    override val default: Dp = 16.dp,
    override val large: Dp = 24.dp,
    override val xLarge: Dp = 32.dp
) : Dimensions

data class MediumDimensions(
    override val xSmall: Dp = 2.dp,
    override val small: Dp = 4.dp,
    override val medium: Dp = 8.dp,
    override val default: Dp = 16.dp,
    override val large: Dp = 24.dp,
    override val xLarge: Dp = 32.dp
) : Dimensions

data class ExpandedDimensions(
    override val xSmall: Dp = 2.dp,
    override val small: Dp = 4.dp,
    override val medium: Dp = 8.dp,
    override val default: Dp = 16.dp,
    override val large: Dp = 24.dp,
    override val xLarge: Dp = 32.dp
) : Dimensions

object LocalDimensionsInstance {
    fun instance(sizeClass: WindowSizeClass): Dimensions {
        return when (sizeClass.widthSizeClass) {
            Compact ->
                CompactDimensions()

            Medium ->
                MediumDimensions()

            Expanded ->
                ExpandedDimensions()

            else -> CompactDimensions()
        }
    }
}