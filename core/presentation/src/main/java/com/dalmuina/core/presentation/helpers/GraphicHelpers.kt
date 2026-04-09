package com.dalmuina.core.presentation.helpers

import java.time.Instant
import java.time.ZoneId

fun formatDayLabel(dayStart: Long): String {
    return Instant.ofEpochMilli(dayStart)
        .atZone(ZoneId.systemDefault())
        .dayOfMonth
        .toString()
}