package com.bussiness.curemegptapp.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun VoiceWaveform(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6B4EFF),
    isRecording: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .width(100.dp)
            .height(40.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // Draw multiple wave lines
        for (i in 0..20) {
            val x = (i / 20f) * width
            val frequency = 0.05f
            val amplitude = if (isRecording) 15f else 5f

            // Calculate wave height with animated phase
            val waveHeight = sin((i * frequency + phase * 0.01f)) * amplitude

            drawLine(
                color = color,
                start = Offset(x, centerY - waveHeight),
                end = Offset(x, centerY + waveHeight),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }
}