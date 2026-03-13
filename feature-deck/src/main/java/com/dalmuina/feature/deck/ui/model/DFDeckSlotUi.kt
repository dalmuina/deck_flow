package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DFDeck

@Immutable
data class DFDeckSlotUi (
    val id: Int,
    val name: String,
    val cardCount: Int,
    val isSelected: Boolean,
)

fun DFDeck.toDeckUi(): DFDeckSlotUi {
    return DFDeckSlotUi(
        id = id,
        name = name,
        cardCount = cards.size,
        isSelected = false,
    )
}
