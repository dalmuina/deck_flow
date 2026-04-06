package com.dalmuina.feature.card.presentation

import androidx.compose.runtime.Stable
import com.dalmuina.feature.card.model.CardUi

@Stable
data class SessionState(
    val loading: Boolean = false,
    val name: String = "",
    val cards : List<CardUi> = emptyList(),
    val isDeckSelected: Boolean = false,
)
