package com.dalmuina.feature.deck.ui.deckSelector

import com.dalmuina.feature.deck.ui.model.DFDeckUi

data class DeckSelectorUiState(
    val loading: Boolean = false,
    val deckList: List<DFDeckUi> = emptyList(),
)
