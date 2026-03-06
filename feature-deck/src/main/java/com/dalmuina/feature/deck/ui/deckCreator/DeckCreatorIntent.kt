package com.dalmuina.feature.deck.ui.deckCreator

sealed interface DeckCreatorIntent {
    data class SelectedCard(val id:Int): DeckCreatorIntent
    data object DeckCreated: DeckCreatorIntent
}