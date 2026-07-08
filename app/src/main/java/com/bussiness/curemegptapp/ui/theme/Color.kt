package com.bussiness.curemegptapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val GradientStart = Color(0xFF4338CA)
val GradientEnd = Color(0xFF211C64)
val GradientRedStart = Color(0xFFF31D1D)
val GradientRedEnd = Color(0xFF8D1111)
val Grey = Color(0xFFC3C6CB)
val GradientLiteBlueStart = Color(0xFF463CC5)
val GradientLiteBlueEnd = Color(0xFF4D42D4)

val AppGradientColors = listOf(GradientStart, GradientEnd)
val AppGradientColors2 = listOf(GradientRedStart, GradientRedEnd)
val AppGradientBlueColors3 = listOf(GradientLiteBlueStart, GradientLiteBlueEnd)

val gradientBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF4338CA),
        Color(0xFF211C64)
    )
)
