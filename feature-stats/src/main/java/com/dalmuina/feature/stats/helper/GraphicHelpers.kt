package com.dalmuina.feature.stats.helper

import com.dalmuina.core.presentation.helpers.formatDayLabel
import com.dalmuina.feature.stats.model.DailyStatsUi
import com.dalmuina.feature.stats.model.StatsBarPoint

fun List<DailyStatsUi>.toBarPoints(): List<StatsBarPoint> =
    map { stat ->
        StatsBarPoint(
            label = formatDayLabel(stat.dayStart),
            value = stat.totalSpentMillis / 60000f
        )
    }