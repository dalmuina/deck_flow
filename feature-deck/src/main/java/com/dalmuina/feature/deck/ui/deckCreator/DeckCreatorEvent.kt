package com.dalmuina.feature.deck.ui.deckCreator

sealed interface DeckCreatorEvent {
    data object CloseScreen : DeckCreatorEvent
}