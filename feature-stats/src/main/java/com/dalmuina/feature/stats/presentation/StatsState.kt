package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Stable
import com.dalmuina.feature.stats.model.CardOptionUi
import com.dalmuina.feature.stats.model.DailyStatsUi
import com.dalmuina.feature.stats.model.DeckOptionUi

@Stable
data class StatsState(
    val loading: Boolean = false,
    val deckOptions: List<DeckOptionUi> = emptyList(),
    val selectedDeckId: Int? = null,
    val cardOptions: List<CardOptionUi> = emptyList(),
    val selectedCardId: Int? = null,
    val dailyStats: List<DailyStatsUi> = emptyList(),
    val statsLoading: Boolean = false,
)
