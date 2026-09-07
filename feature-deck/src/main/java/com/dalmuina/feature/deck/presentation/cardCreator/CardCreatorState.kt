package com.dalmuina.feature.deck.presentation.cardCreator

import kotlin.time.Duration

data class CardCreatorState(
    val loading: Boolean = false,
    val processing: Boolean = false,
    val name: String = "",
    val duration: Duration = Duration.ZERO,
)
