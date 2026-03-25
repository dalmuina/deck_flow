package com.dalmuina.core.utils

import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

fun isToday(timestamp: Long, now: Long): Boolean {
    val nowCal = Calendar.getInstance().apply { timeInMillis = now }
    val tsCal = Calendar.getInstance().apply { timeInMillis = timestamp }

    return nowCal.get(Calendar.YEAR) == tsCal.get(Calendar.YEAR) &&
            nowCal.get(Calendar.DAY_OF_YEAR) == tsCal.get(Calendar.DAY_OF_YEAR)
}

fun Long.startOfDayMillis(): Long {
    val zoneId = ZoneId.systemDefault()
    return Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()
}
