package com.dalmuina.feature.deck.ui.deckSelector

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.deck.ui.model.DFDeckSlotUi

@Immutable
data class DeckSelectorUiState(
    val loading: Boolean = false,
    val deckList: List<DFDeckSlotUi> = emptyList(),
)
