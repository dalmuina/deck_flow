package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DailyStatsDomain

@Immutable
data class DailyStatsUi(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
)

fun DailyStatsDomain.toUi(): DailyStatsUi =
    DailyStatsUi(
        dayStart = dayStart,
        totalSpentMillis = totalSpentMillis,
        completedCount = completedCount,
    )
