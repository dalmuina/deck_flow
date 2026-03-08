package com.dalmuina.feature.deck.ui.deckCreator

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.deck.ui.model.DFCardUi

@Immutable
data class DeckCreatorUiState(
    val loading: Boolean = false,
    val name: String = "",
    val deckCard: List<DFCardUi> = emptyList(),
    val isEditMode: Boolean = false,
)
