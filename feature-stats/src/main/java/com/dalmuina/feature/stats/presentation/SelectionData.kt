package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.CardOptionUi

@Immutable
data class SelectionData(
    val loading: Boolean = false,
    val activityOptions: List<CardOptionUi> = emptyList(),
    val selectedCardId: Int? = null,
    val selectedCardDurationMillis: Long = 0L,
)
