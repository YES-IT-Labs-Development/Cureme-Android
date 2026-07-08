// StatusBarController.kt
package com.bussiness.curemegptapp.ui.utils

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun TransparentStatusBar(
    darkIcons: Boolean = true  // true = dark icons, false = light icons
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar को transparent करें
            window.statusBarColor = Color.Transparent.toArgb()

            // Status bar icons का color set करें
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = darkIcons

                // Navigation bar icons (optional)
                isAppearanceLightNavigationBars = darkIcons
            }
        }
    }
}

@Composable
fun DynamicStatusBar(
    statusBarColor: Color = Color.Transparent,
    darkIcons: Boolean = true
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(statusBarColor, darkIcons) {
            val window = (view.context as Activity).window

            // Status bar color set करें
            window.statusBarColor = statusBarColor.toArgb()

            // Status bar icons का color set करें
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = darkIcons

            onDispose { /* Cleanup if needed */ }
        }
    }
}

@Composable
fun AutoStatusBar(
    backgroundColor: Color = Color.White
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar color set करें
            window.statusBarColor = backgroundColor.toArgb()

            // Automatically decide icon color based on background
            val isDarkBackground = backgroundColor.luminance < 0.5f
            val darkIcons = !isDarkBackground  // Light background = dark icons

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = darkIcons
                isAppearanceLightNavigationBars = darkIcons
            }
        }
    }
}

// Color extension for luminance calculation
val Color.luminance: Float
    get() = (0.299f * red + 0.587f * green + 0.114f * blue)