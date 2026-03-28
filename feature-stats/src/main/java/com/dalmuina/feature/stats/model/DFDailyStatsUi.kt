package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DFDailyStatsDomain

@Immutable
data class DFDailyStatsUi(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
)

fun DFDailyStatsDomain.toUi(): DFDailyStatsUi =
    DFDailyStatsUi(
        dayStart = dayStart,
        totalSpentMillis = totalSpentMillis,
        completedCount = completedCount,
    )
