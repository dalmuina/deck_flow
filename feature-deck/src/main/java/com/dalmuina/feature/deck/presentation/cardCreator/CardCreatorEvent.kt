package com.dalmuina.feature.deck.presentation.cardCreator

sealed interface CardCreatorEvent {
    data class CloseScreen(val cardId: Int?) : CardCreatorEvent
}