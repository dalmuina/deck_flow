package com.dalmuina.data.entity

import com.dalmuina.domain.model.DailyStatsDomain

data class DailyStatsEntity(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
)

fun DailyStatsEntity.toDomain(): DailyStatsDomain =
    DailyStatsDomain(
        dayStart = dayStart,
        totalSpentMillis = totalSpentMillis,
        completedCount = completedCount,
    )
