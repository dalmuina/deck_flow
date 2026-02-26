package com.dalmuina.feature.deck.ui.cardCreator

sealed interface CardCreatorEvent {
    data object CloseScreen : CardCreatorEvent
}