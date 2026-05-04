package com.dalmuina.feature.card.presentation.session

import androidx.compose.runtime.Stable
import com.dalmuina.feature.card.model.CardCompletionPending
import com.dalmuina.feature.card.model.CardUi

@Stable
data class CardState(
    val loading: Boolean = false,
    val name: String = "",
    val cards : List<CardUi> = emptyList(),
    val isDeckSelected: Boolean = false,
    val completionPending: CardCompletionPending? = null,
)
