package com.dalmuina.feature.deck.presentation.deckCreator

sealed interface DeckCreatorEvent {
    data object CloseScreen : DeckCreatorEvent
}