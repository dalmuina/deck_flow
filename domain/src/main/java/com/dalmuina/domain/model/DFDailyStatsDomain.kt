package com.dalmuina.domain.model

data class DFDailyStatsDomain(
    val dayStart: Long,
    val totalSpentMillis: Long,
    val completedCount: Int,
)
