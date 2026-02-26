package com.dalmuina.feature.deck.ui.deckCreator

import com.dalmuina.feature.deck.ui.model.DFCardUi

data class DeckCreatorUiState(
    val loading: Boolean = false,
    val deckCard: List<DFCardUi> = emptyList(),
)
