package com.dalmuina.feature.deck.presentation.deckCreator

import androidx.compose.runtime.Stable
import com.dalmuina.feature.deck.model.CardUi

@Stable
data class DeckCreatorState(
    val loading: Boolean = false,
    val name: String = "",
    val deckCard: List<CardUi> = emptyList(),
    val cardPendingDelete: CardUi? = null,
)
