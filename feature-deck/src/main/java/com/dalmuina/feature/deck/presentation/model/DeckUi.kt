package com.dalmuina.feature.deck.presentation.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DeckDomain

@Immutable
data class DeckUi (
    val id: Int,
    val name: String,
    val cardCount: Int,
    val isSelected: Boolean,
)

fun DeckDomain.toUi(): DeckUi {
    return DeckUi(
        id = id,
        name = name,
        cardCount = cards.size,
        isSelected = false,
    )
}
