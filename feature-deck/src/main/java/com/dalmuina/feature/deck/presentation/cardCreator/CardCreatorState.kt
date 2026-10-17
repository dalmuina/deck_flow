package com.dalmuina.feature.deck.presentation.cardCreator

data class CardCreatorState(
    val loading: Boolean = false,
    val processing: Boolean = false,
    val name: String = "",
    val description: String = "",
)
