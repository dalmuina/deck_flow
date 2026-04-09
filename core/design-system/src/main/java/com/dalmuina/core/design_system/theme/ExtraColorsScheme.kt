package com.dalmuina.core.design_system.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class DFExtraColors(
    val cardSlotSelectedContainer: Color,
    val cardSlotUnselectedContainer: Color,
    val cardSlotSelectedContent: Color,
    val cardSlotUnselectedContent: Color,
)

val LocalDFExtraColors = staticCompositionLocalOf {
    DFExtraColors(
        cardSlotSelectedContainer = Color.Unspecified,
        cardSlotUnselectedContainer = Color.Unspecified,
        cardSlotSelectedContent = Color.Unspecified,
        cardSlotUnselectedContent = Color.Unspecified,
    )
}
