package com.sm.voicerecorder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sm.voicerecorder.model.AppProfile

// =======================
// Temas Mágicos (Kids)
// =======================
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

// =======================
// Temas Padrão (Adulto)
// =======================
private val DefaultLightColorScheme = lightColorScheme(
    primary = DefaultPrimary,
    onPrimary = DefaultOnPrimary,
    primaryContainer = DefaultPrimaryContainer,
    onPrimaryContainer = DefaultOnPrimaryContainer,
    secondary = DefaultSecondary,
    onSecondary = DefaultOnSecondary,
    secondaryContainer = DefaultSecondaryContainer,
    onSecondaryContainer = DefaultOnSecondaryContainer,
    tertiary = DefaultTertiary,
    background = DefaultBackground,
    onBackground = DefaultOnSurface,
    surface = DefaultSurface,
    onSurface = DefaultOnSurface,
    surfaceVariant = DefaultSurfaceVariant,
    onSurfaceVariant = DefaultOnSurfaceVariant
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DefaultDarkPrimary,
    onPrimary = Color(0xFF003258),
    primaryContainer = DefaultDarkPrimaryContainer,
    onPrimaryContainer = DefaultDarkOnSurface,
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF263238),
    secondaryContainer = Color(0xFF37474F),
    onSecondaryContainer = Color(0xFFCFD8DC),
    tertiary = DefaultTertiary,
    background = DefaultDarkBackground,
    onBackground = DefaultDarkOnSurface,
    surface = DefaultDarkSurface,
    onSurface = DefaultDarkOnSurface,
    surfaceVariant = DefaultDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF9E9E9E)
)

@Composable
fun VoiceRecorderTheme(
    profile: AppProfile = AppProfile.KIDS,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (profile) {
        AppProfile.KIDS -> if (darkTheme) MagicDarkColorScheme else MagicLightColorScheme
        AppProfile.DEFAULT -> if (darkTheme) DefaultDarkColorScheme else DefaultLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
