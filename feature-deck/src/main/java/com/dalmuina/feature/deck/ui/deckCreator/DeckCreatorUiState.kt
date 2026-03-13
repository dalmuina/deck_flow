package com.dalmuina.feature.deck.ui.deckCreator

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.deck.ui.model.DFCardSlotUi

@Immutable
data class DeckCreatorUiState(
    val loading: Boolean = false,
    val name: String = "",
    val deckCard: List<DFCardSlotUi> = emptyList(),
    val isEditMode: Boolean = false,
)
