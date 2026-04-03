package com.dalmuina.feature.deck.presentation.deckCreator

sealed interface DeckCreatorIntent {
    data class SelectedCard(val id:Int): DeckCreatorIntent
    data class CardCreated(val id: Int) : DeckCreatorIntent
    data object SaveDeck: DeckCreatorIntent
    data class NameChanged(val value: String) : DeckCreatorIntent
    data class DeleteCard(val id: Int): DeckCreatorIntent
    data class Reorder(val from: Int, val to: Int) : DeckCreatorIntent
}