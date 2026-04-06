package com.dalmuina.feature.stats.helpers

import com.dalmuina.feature.stats.model.DailyStatsUi
import com.dalmuina.feature.stats.model.StatsBarPoint
import java.time.Instant
import java.time.ZoneId

fun List<DailyStatsUi>.toBarPoints(): List<StatsBarPoint> =
    map { stat ->
        StatsBarPoint(
            label = formatDayLabel(stat.dayStart),
            value = stat.totalSpentMillis / 60000f
        )
    }

fun formatDayLabel(dayStart: Long): String {
    return Instant.ofEpochMilli(dayStart)
        .atZone(ZoneId.systemDefault())
        .dayOfMonth
        .toString()
}