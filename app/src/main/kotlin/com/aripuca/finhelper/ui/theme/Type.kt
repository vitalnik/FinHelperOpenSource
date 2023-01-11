package com.aripuca.finhelper.ui.theme

import androidx.compose.material3.Typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.R

val Roboto = FontFamily(
    Font(R.font.roboto_medium),
    Font(R.font.roboto_medium_italic, style = FontStyle.Italic),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_bold_italic, style = FontStyle.Italic, weight = FontWeight.Bold),
)

val MainHeader = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.5.sp,
    fontSize = 36.sp
)

// Set of Material typography styles to start with
val Typography = Typography()