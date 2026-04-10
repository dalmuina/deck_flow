package com.dalmuina.feature.deck.presentation.deckSelector

import com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorIntent

sealed interface DeckSelectorIntent {
    data class SelectDeck(val deckId: Int): DeckSelectorIntent

    data class RequestDeleteDeck(val id: Int) : DeckSelectorIntent
    data object ConfirmDeleteDeck : DeckSelectorIntent
    data object DismissDeleteDialog : DeckSelectorIntent
}