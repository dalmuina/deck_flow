package com.dalmuina.feature.deck.presentation.deckSelector

sealed interface DeckSelectorIntent {
    data class SelectDeck(
        val deckId: Int,
    ) : DeckSelectorIntent

    data class RequestDeleteDeck(
        val id: Int,
    ) : DeckSelectorIntent

    data object ConfirmDeleteDeck : DeckSelectorIntent

    data object DismissDeleteDialog : DeckSelectorIntent
}
