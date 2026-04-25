package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.stats.model.MonthHeatmapDayUi

@Immutable
data class StatsData(
    val loading: Boolean = false,
    val year: Int = 0,
    val month: Int = 0,
    val heatmapDays: List<MonthHeatmapDayUi> = emptyList(),
    val hasPreviousData: Boolean = false,
    val hasNextMonth: Boolean = false,
)
