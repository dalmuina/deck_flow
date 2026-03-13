package com.dalmuina.feature.deck.ui.cardCreator

sealed interface CardCreatorEvent {
    data class CloseScreen(val cardId: Int) : CardCreatorEvent
}