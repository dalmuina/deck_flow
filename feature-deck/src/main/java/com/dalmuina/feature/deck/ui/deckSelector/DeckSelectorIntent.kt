package com.dalmuina.feature.deck.ui.deckSelector

sealed interface DeckSelectorIntent {
    data object LoadDecks: DeckSelectorIntent
}