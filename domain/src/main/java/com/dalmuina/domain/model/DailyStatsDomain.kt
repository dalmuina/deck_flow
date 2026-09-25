package com.dalmuina.domain.model

data class DailyStatsDomain(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
)
