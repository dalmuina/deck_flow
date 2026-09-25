package com.dalmuina.feature.deck.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DeckDomain

@Immutable
data class DeckUi(
    val id: Int,
    val name: String,
    val cardCount: Int,
    val isSelected: Boolean,
)

fun DeckDomain.toUi(): DeckUi =
    DeckUi(
        id = id,
        name = name,
        cardCount = cards.size,
        isSelected = false,
    )
