package com.dalmuina.feature.deck.ui.deckCreator

sealed interface DeckCreatorIntent {
    data class CardClicked(val id:Int): DeckCreatorIntent
}