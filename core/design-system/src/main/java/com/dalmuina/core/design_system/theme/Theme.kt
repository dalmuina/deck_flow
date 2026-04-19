package com.dalmuina.core.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Theme.kt
private val LightColorScheme = lightColorScheme(
    primary        = Indigo500,       // botón primario, acentos activos
    onPrimary      = NeutralWhite,    // texto/icono sobre primary
    primaryContainer   = Indigo100,   // botón secundario, chips, fondo badge
    onPrimaryContainer = Indigo500,   // icono dentro del botón secundario

    secondary      = PrimaryLightColor,   // acciones secundarias, FAB alt
    onSecondary    = NeutralWhite,
    tertiary       = TertiaryLightColor,

    background     = NeutralSurface,  // fondo de pantalla (el gris f2f2f7)
    onBackground   = Color(0xFF1C1C1E),

    surface        = NeutralCard,     // cards, bottom sheets, dialogs
    onSurface      = Color(0xFF1C1C1E),
    surfaceVariant = Indigo100,       // chips, badges, input backgrounds
    onSurfaceVariant = Color(0xFF5C5C7A),

    outline        = DarkBorder,   // bordes de cards y inputs
    outlineVariant = Color(0xFFE8E8EE),
)

private val DarkColorScheme = darkColorScheme(
    primary        = PrimaryDarkColor,    // tu lila existente
    onPrimary      = Color(0xFF1C1C2E),
    primaryContainer   = Color(0xFF2A2A4E),
    onPrimaryContainer = PrimaryDarkColor,

    secondary      = SecondaryDarkColor,
    onSecondary    = Color(0xFF1C1C2E),
    tertiary       = TertiaryDarkColor,

    background     = DarkBackground,  // casi negro
    onBackground   = OnDarkPrimary,

    surface        = DarkCard,        // cards oscuras
    onSurface      = OnDarkPrimary,
    surfaceVariant = DarkSurface,     // contenedores secundarios
    onSurfaceVariant = Color(0xFF94A3B8),

    outline        = NeutralBorder,
    outlineVariant = Color(0xFF2A2A3E),
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