package com.dalmuina.feature.deck.ui.deckSelector

import com.dalmuina.feature.deck.ui.model.DeckUi

data class DeckSelectorUiState(
    val loading: Boolean = false,
    val deckList: List<DeckUi> = emptyList(),
)
