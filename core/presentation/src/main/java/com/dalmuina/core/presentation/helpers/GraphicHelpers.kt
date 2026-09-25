package com.dalmuina.core.presentation.helpers

import java.time.Instant
import java.time.ZoneId

fun formatDayLabel(dayStart: Long): String =
    Instant
        .ofEpochMilli(dayStart)
        .atZone(ZoneId.systemDefault())
        .dayOfMonth
        .toString()

fun Long.toHeatmapLevel(targetMillis: Long): Int {
    if (targetMillis <= 0L || this <= 0L) return 0
    val ratio = this.toFloat() / targetMillis.toFloat()
    return when {
        ratio < 0.01f -> 0
        ratio < 0.34f -> 1
        ratio < 0.67f -> 2
        ratio < 1.00f -> 3
        else -> 4
    }
}
