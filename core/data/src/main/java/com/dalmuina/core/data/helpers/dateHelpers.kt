package com.dalmuina.core.data.helpers

import java.time.Instant
import java.time.ZoneId

fun Long.startOfDayMillis(): Long {
    val zoneId = ZoneId.systemDefault()
    return Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()
}
