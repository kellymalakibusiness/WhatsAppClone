package com.malakiapps.whatsappclone.android.presentation

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

@Composable
fun FakeWhatsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF21c063),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF0b1014),
            onPrimaryContainer = Color(0xFF23282c),
            secondaryContainer = Color(0xFF134d37),
            secondary = Color(0xFF8d9598),
            onSecondary = Color(0xFF1f272a),
            tertiary = Color.White,
            onTertiary = Color(0xFF28be65),
            tertiaryContainer = Color(0xFF103629),
            onTertiaryContainer = Color(0xFFd8fdd2),
            background = Color(0xFF0b1014),
            onBackground = Color.White,
            surface = Color(0xFF0b1014),
            onSurface = Color.White,
            inverseSurface = Color.Black,
            surfaceTint = Color(0xFF103629)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1dab61),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFf5f2eb),
            onPrimaryContainer = Color(0xFFe9e1d6),
            secondaryContainer = Color(0xFFd8fdd2),
            secondary = Color(0xFF5C6064),
            onSecondary = Color(0xFFf1f2f4),
            tertiary = Color(0xFF1bac61),
            onTertiary = Color(0xFF15603f),
            tertiaryContainer = Color(0xFFd8fdd2),
            onTertiaryContainer = Color(0xFF15603f),
            onBackground = Color.Black,
            inverseSurface = Color(0xFFd5d6d8).copy(0.2f),
            surfaceTint = Color.White
        )
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )

    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(16.dp)
    )

    //Status bar color
    val activity = LocalContext.current as? Activity
    activity?.window?.let { window ->
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = colors.background.toArgb()
        
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
