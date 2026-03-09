package com.dalmuina.feature.deck.ui.deckSelector

sealed interface DeckSelectorIntent {
    data class DeleteDeck(val deckId: Int): DeckSelectorIntent
}