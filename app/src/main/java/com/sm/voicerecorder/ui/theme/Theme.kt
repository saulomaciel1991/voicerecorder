package com.sm.voicerecorder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MagicLightColorScheme = lightColorScheme(
    primary = MagicLilacPrimary,
    onPrimary = MagicLilacOnPrimary,
    primaryContainer = MagicLilacPrimaryContainer,
    onPrimaryContainer = MagicLilacOnPrimaryContainer,
    secondary = MagicPinkSecondary,
    onSecondary = MagicPinkOnSecondary,
    secondaryContainer = MagicPinkSecondaryContainer,
    onSecondaryContainer = MagicPinkOnSecondaryContainer,
    tertiary = MagicGoldTertiary,
    onTertiary = MagicGoldOnTertiary,
    background = MagicBackground,
    onBackground = MagicOnSurface,
    surface = MagicSurface,
    onSurface = MagicOnSurface,
    surfaceVariant = MagicSurfaceVariant,
    onSurfaceVariant = MagicOnSurfaceVariant
)

private val MagicDarkColorScheme = darkColorScheme(
    primary = MagicDarkPrimary,
    onPrimary = Color(0xFF38004D),
    primaryContainer = MagicDarkPrimaryContainer,
    onPrimaryContainer = MagicDarkOnSurface,
    secondary = Color(0xFFFF80AB),
    onSecondary = Color(0xFF560027),
    secondaryContainer = Color(0xFF880E4F),
    onSecondaryContainer = Color(0xFFFFD8E4),
    tertiary = MagicGoldTertiary,
    background = MagicDarkBackground,
    onBackground = MagicDarkOnSurface,
    surface = MagicDarkSurface,
    onSurface = MagicDarkOnSurface,
    surfaceVariant = MagicDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD1C4E9)
)

@Composable
fun VoiceRecorderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MagicDarkColorScheme else MagicLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
