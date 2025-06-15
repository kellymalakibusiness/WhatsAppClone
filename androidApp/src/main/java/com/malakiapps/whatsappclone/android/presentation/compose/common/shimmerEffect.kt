package com.malakiapps.whatsappclone.android.presentation.compose.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(shape: Shape): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue =   2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000
            )
        )
    )

    background(
        brush = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),//Color(0xFF8F8B8B),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
            ),
            startX = startOffsetX,
            endX = startOffsetX + size.width
        ),
        shape = shape
    )
        .onGloballyPositioned {
            size = it.size
        }
}