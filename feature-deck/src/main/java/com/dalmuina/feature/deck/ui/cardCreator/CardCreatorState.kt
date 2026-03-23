package com.dalmuina.feature.deck.ui.cardCreator

import androidx.compose.runtime.Immutable
import kotlin.time.Duration

@Immutable
data class CardCreatorState(
    val loading: Boolean = false,
    val processing: Boolean = false,
    val name: String = "",
    val duration: Duration = Duration.ZERO
)
