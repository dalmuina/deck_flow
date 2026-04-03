package com.dalmuina.feature.deck.presentation.deckSelector

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.deck.presentation.model.DFDeckUi

@Immutable
data class DeckSelectorState(
    val loading: Boolean = false,
    val deckList: List<DFDeckUi> = emptyList(),
)
