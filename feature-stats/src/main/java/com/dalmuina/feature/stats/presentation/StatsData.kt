package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.DailyStatsUi

@Immutable
data class StatsData(
    val loading: Boolean = false,
    val dailyStats: List<DailyStatsUi> = emptyList(),
)
