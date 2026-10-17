package com.dalmuina.feature.deck.presentation.cardCreator

sealed interface CardCreatorIntent {
    data class NameChanged(
        val value: String,
    ) : CardCreatorIntent

    data class DescriptionChanged(
        val value: String,
    ) : CardCreatorIntent

    data object SaveActivity : CardCreatorIntent

    data object Cancel : CardCreatorIntent
}
