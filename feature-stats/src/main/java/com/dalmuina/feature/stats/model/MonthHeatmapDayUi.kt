package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable

@Immutable
data class MonthHeatmapDayUi(
    val dayOfMonth: Int,
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
    val level: Int,
)
