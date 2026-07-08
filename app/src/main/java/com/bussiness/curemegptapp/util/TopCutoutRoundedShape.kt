package com.bussiness.curemegptapp.util

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection


class TopCutoutRoundedShape(
    private val cornerRadius: Float = 40f,
    private val cutoutWidth: Float = 220f,
    private val cutoutHeight: Float = 40f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val path = Path()

        val left = 0f
        val right = size.width
        val top = 0f
        val bottom = size.height

        val cutoutStartX = (size.width - cutoutWidth) / 2f
        val cutoutEndX = cutoutStartX + cutoutWidth

        path.moveTo(left + cornerRadius, top)

        // Top left rounded corner
        path.quadraticTo(left, top, left, top + cornerRadius)

        // Left vertical side
        path.lineTo(left, bottom - cornerRadius)

        // Bottom left corner
        path.quadraticTo(left, bottom, left + cornerRadius, bottom)

        // Bottom edge
        path.lineTo(right - cornerRadius, bottom)

        // Bottom right corner
        path.quadraticTo(right, bottom, right, bottom - cornerRadius)

        // Right vertical
        path.lineTo(right, top + cornerRadius)

        // Top right corner
        path.quadraticTo(right, top, right - cornerRadius, top)

        // Move to start of cutout
        path.lineTo(cutoutEndX, top)

        // Cutout right curve
        path.quadraticTo(
            cutoutEndX - (cutoutWidth * 0.15f),
            top + cutoutHeight,
            (cutoutStartX + cutoutEndX) / 2f,
            top + cutoutHeight
        )

        // Cutout left curve
        path.quadraticTo(
            cutoutStartX + (cutoutWidth * 0.15f),
            top + cutoutHeight,
            cutoutStartX,
            top
        )

        // Back to left top rounded corner
        path.lineTo(left + cornerRadius, top)

        path.close()
        return Outline.Generic(path)
    }
}
