package com.dalmuina.feature.card.model

import androidx.compose.runtime.Stable
import kotlin.time.Duration

@Stable
data class CardCompletionPending(
    val cardId: Int,
    val cardName: String,
    val spentDuration: Duration,
)
