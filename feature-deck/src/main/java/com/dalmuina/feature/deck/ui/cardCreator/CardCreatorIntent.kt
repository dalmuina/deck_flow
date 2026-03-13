package com.dalmuina.feature.deck.ui.cardCreator

sealed interface CardCreatorIntent {
    data class NameChanged(val value: String): CardCreatorIntent
    data class TimeChanged(val value: String): CardCreatorIntent
    data object MoreTime: CardCreatorIntent
    data object LessTime: CardCreatorIntent
    data object SaveActivity: CardCreatorIntent
}
