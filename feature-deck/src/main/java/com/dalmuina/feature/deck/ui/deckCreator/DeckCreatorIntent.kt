package com.dalmuina.feature.deck.ui.deckCreator

sealed interface DeckCreatorIntent {
    data object Create: DeckCreatorIntent
    data class CardClicked(val id:String): DeckCreatorIntent
}