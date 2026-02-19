package com.dalmuina.feature.deck.ui.deckCreator

import com.dalmuina.feature.deck.ui.model.CardUi

data class DeckCreatorUiState(
    val loading: Boolean = false,
    val deckCard: List<CardUi> = emptyList(),
)
