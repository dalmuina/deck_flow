package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.DFDailyStatsUi

@Immutable
data class StatsData(
    val loading: Boolean = false,
    val dailyStats: List<DFDailyStatsUi> = emptyList(),
)
