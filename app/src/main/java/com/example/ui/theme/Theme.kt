package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BcaElectricCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF004B87),
    onPrimaryContainer = Color(0xFFD6EEFF),
    secondary = BcaSoftSky,
    onSecondary = Color(0xFF041E3A),
    secondaryContainer = Color(0xFF0E3963),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = CoralExpense,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4C0D1D),
    onTertiaryContainer = Color(0xFFFECDD3),
    background = DarkBackground,
    onBackground = Color(0xFFF0F7FF),
    surface = DarkSurface,
    onSurface = Color(0xFFF0F7FF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF9CC2E6),
    outline = DarkOutline,
    error = CoralExpense
)

private val LightColorScheme = lightColorScheme(
    primary = BcaRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF0FF),
    onPrimaryContainer = Color(0xFF00356B),
    secondary = BcaCobaltBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF0A2E52),
    tertiary = CoralExpense,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E6),
    onTertiaryContainer = Color(0xFF881337),
    background = LightBackground,
    onBackground = BcaTextNavy,
    surface = LightSurface,
    onSurface = BcaTextNavy,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = BcaTextSlate,
    outline = LightOutline,
    error = CoralExpense
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to clean minimalist myBCA Blue & White Glassmorphism
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
