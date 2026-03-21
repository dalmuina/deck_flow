package com.dalmuina.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object DFTheme {
    val extraColors: DFExtraColors
        @Composable
        get() = LocalDFExtraColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = Melrose,
    secondary = LavenderGray,
    tertiary = BeautyBush,
)

private val LightColorScheme = lightColorScheme(
    primary = Cadillac,
    secondary = Smoky,
    tertiary = Ferra
)

private val LightExtraColors = DFExtraColors(
    cardSlotSelectedContainer = Viking,
    cardSlotUnselectedContainer = AthensGray,
    cardSlotSelectedContent = BalticSea,
    cardSlotUnselectedContent = Mako,
)

private val DarkExtraColors = DFExtraColors(
    cardSlotSelectedContainer = Melrose,
    cardSlotUnselectedContainer = BalticSea,
    cardSlotSelectedContent = AthensGray,
    cardSlotUnselectedContent = GreySuit,
)

@Composable
fun DeckFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(
        LocalDFExtraColors provides extraColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}