package com.dalmuina.feature.deck.ui.cardCreator

import kotlin.time.Duration

data class CardCreatorUiState(
    val loading: Boolean = false,
    val title: String = "",
    val duration: Duration = Duration.ZERO
)
