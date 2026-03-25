package com.dalmuina.data.entity

data class DFDailyStatsEntity(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int
)