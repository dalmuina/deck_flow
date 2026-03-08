package com.dalmuina.feature.deck.ui.deckCreator

sealed interface DeckCreatorIntent {
    data class SelectedCard(val id:Int): DeckCreatorIntent
    data object SaveDeck: DeckCreatorIntent
    data class NameChanged(val value: String) : DeckCreatorIntent
}