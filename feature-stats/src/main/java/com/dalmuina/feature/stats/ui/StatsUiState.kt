package com.dalmuina.feature.stats.ui

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.DFCardOptionUi
import com.dalmuina.feature.stats.model.DFDailyStatsUi
import com.dalmuina.feature.stats.model.DFDeckOptionUi

@Immutable
data class StatsUiState(
    val loading: Boolean = false,
    val deckOptions: List<DFDeckOptionUi> = emptyList(),
    val selectedDeckId: Int? = null,
    val cardOptions: List<DFCardOptionUi> = emptyList(),
    val selectedCardId: Int? = null,
    val dailyStats: List<DFDailyStatsUi> = emptyList(),
    val statsLoading: Boolean = false,
)
