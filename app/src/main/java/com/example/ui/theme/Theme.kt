package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IslamicDarkColorScheme = darkColorScheme(
    primary = IslamicGold,
    onPrimary = EmeraldDeepBg,
    primaryContainer = EmeraldSurfaceVariant,
    onPrimaryContainer = IslamicGoldLight,
    secondary = IslamicGoldLight,
    onSecondary = EmeraldDeepBg,
    secondaryContainer = EmeraldSurface,
    onSecondaryContainer = TextWarmWhite,
    background = EmeraldDeepBg,
    onBackground = TextWarmWhite,
    surface = EmeraldSurface,
    onSurface = TextWarmWhite,
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = TextMutedGreen,
    error = StatusError,
    onError = EmeraldDeepBg,
    outline = EmeraldBorder
)

private val IslamicLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = TextWarmWhite,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = LightAccent,
    onSecondary = TextWarmWhite,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = TextDark,
    background = LightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4A6155),
    error = StatusError,
    onError = TextWarmWhite,
    outline = Color(0xFFCAD7D0)
)

@Composable
fun QuranifyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) IslamicDarkColorScheme else IslamicLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
