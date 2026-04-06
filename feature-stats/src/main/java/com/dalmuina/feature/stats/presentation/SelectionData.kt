package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.CardOptionUi
import com.dalmuina.feature.stats.model.DeckOptionUi

@Immutable
data class SelectionData(
    val loading: Boolean = false,
    val deckOptions: List<DeckOptionUi> = emptyList(),
    val selectedDeckId: Int? = null,
    val cardOptions: List<CardOptionUi> = emptyList(),
    val selectedCardId: Int? = null,
)
