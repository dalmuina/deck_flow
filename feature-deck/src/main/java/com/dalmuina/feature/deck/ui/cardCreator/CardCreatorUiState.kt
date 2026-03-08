package com.dalmuina.feature.deck.ui.cardCreator

import androidx.compose.runtime.Immutable
import kotlin.time.Duration

@Immutable
data class CardCreatorUiState(
    val loading: Boolean = false,
    val title: String = "",
    val duration: Duration = Duration.ZERO
)
