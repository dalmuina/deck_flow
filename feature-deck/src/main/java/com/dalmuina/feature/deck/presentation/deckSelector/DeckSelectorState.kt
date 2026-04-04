package com.dalmuina.feature.deck.presentation.deckSelector

import androidx.compose.runtime.Stable
import com.dalmuina.feature.deck.presentation.model.DFDeckUi

@Stable
data class DeckSelectorState(
    val loading: Boolean = false,
    val deckList: List<DFDeckUi> = emptyList(),
)
