package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


class ReceivedMessageShape: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val indicatorLength = with(density){ 8.dp.toPx() }
        val cornerRadius = with(density){ 8.dp.toPx() }
        val path = Path().apply {
            val radius = CornerRadius(cornerRadius, cornerRadius)
            addRoundRect(
                roundRect = RoundRect(
                    left = indicatorLength,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    topRightCornerRadius = radius,
                    topLeftCornerRadius = CornerRadius.Zero,
                    bottomRightCornerRadius = radius,
                    bottomLeftCornerRadius = radius,
                )
            )
            moveTo(0f, 0f)
            lineTo(indicatorLength, 0f)
            lineTo(indicatorLength, indicatorLength * 1.2f)
            close()
        }

        return Outline.Generic(path = path)
    }
}


class SentMessageShape: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val indicatorLength = with(density){ 8.dp.toPx() }
        val cornerRadius = with(density){ 8.dp.toPx() }

        val indicatorPositionX = size.width - indicatorLength
        val path = Path().apply {
            val radius = CornerRadius(cornerRadius, cornerRadius)
            addRoundRect(
                roundRect = RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width-indicatorLength,
                    bottom = size.height,
                    topRightCornerRadius = CornerRadius.Zero,
                    topLeftCornerRadius = radius,
                    bottomRightCornerRadius = radius,
                    bottomLeftCornerRadius = radius,
                )
            )
            moveTo(size.width, 0f)
            lineTo(indicatorPositionX, 0f)
            lineTo(indicatorPositionX, indicatorLength * 1.2f)
            close()
        }

        return Outline.Generic(path = path)
    }
}