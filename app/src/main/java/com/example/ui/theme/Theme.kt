package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimary,
    secondary = GoldAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color(0xFF0F0F0F),
    onSecondary = Color(0xFF0F0F0F),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = Color(0xFF242424),
    onPrimaryContainer = Color(0xFFBB86FC),
    secondaryContainer = Color(0xFF112222),
    onSecondaryContainer = Color(0xFF03DAC6),
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0xFF2A2A2A),
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme
    dynamicColor: Boolean = false, // Use our static colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
