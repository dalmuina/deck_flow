package com.dalmuina.feature.deck.ui.deckCreator

import com.dalmuina.feature.deck.ui.model.DeckUi

data class DeckCreatorUiState(
    val loading: Boolean = false,
    val deckCard: List<String> = emptyList(),
)
