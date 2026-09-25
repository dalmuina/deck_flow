package com.dalmuina.feature.stats.presentation

import androidx.compose.runtime.Stable
import com.dalmuina.feature.stats.model.CardOptionUi
import com.dalmuina.feature.stats.model.MonthHeatmapDayUi

@Stable
data class StatsState(
    val loading: Boolean = false,
    val activityOptions: List<CardOptionUi> = emptyList(),
    val selectedCardId: Int? = null,
    val statsLoading: Boolean = false,
    val year: Int = 0,
    val month: Int = 0,
    val heatmapDays: List<MonthHeatmapDayUi> = emptyList(),
    val hasPreviousData: Boolean = false,
    val hasNextMonth: Boolean = false,
)
