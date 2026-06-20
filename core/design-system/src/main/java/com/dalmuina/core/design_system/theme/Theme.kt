package com.dalmuina.core.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Theme.kt
private val LightColorScheme = lightColorScheme(
    // Primary
    primary             = PrimaryLight,
    onPrimary           = OnPrimaryLight,
    primaryContainer    = PrimaryContainerLight,
    onPrimaryContainer  = PrimaryLight,

    // Secondary
    secondary                = SecondaryLight,
    onSecondary              = OnPrimaryLight,
    secondaryContainer       = SecondaryContainerLight,
    onSecondaryContainer     = OnSecondaryContainerLight,

    // Tertiary
    tertiary = TertiaryLight,

    // Background
    background   = BackgroundLight,
    onBackground = OnBackgroundLight,

    // Surface
    surface          = SurfaceLight,
    onSurface        = OnBackgroundLight,
    surfaceVariant   = PrimaryContainerLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainer = SurfaceContainerLight,

    // Outline
    outline        = BorderDark,

    outlineVariant = BorderLight,
)

private val DarkColorScheme = darkColorScheme(
    // Primary
    primary             = PrimaryDark,
    onPrimary           = OnPrimaryDark,
    primaryContainer    = PrimaryContainerDark,
    onPrimaryContainer  = PrimaryDark,

    // Secondary
    secondary                = SecondaryDark,
    onSecondary              = OnPrimaryDark,
    secondaryContainer       = SecondaryContainerDark,
    onSecondaryContainer     = OnSecondaryContainerDark,

    // Tertiary
    tertiary = TertiaryDark,

    // Background
    background   = BackgroundDark,
    onBackground = OnBackgroundDark,

    // Surface
    surface          = SurfaceDark,
    onSurface        = OnBackgroundDark,
    surfaceVariant   = SurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,

    // Outline
    outline        = BorderLight,
    outlineVariant = BorderDark,
)

@Composable
fun DeckFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}