package com.dalmuina.core.presentation.helpers

import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import kotlin.time.Duration

fun getLast7DaysRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val toDay = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_YEAR, -6)

    val fromDay = calendar.timeInMillis

    return fromDay to toDay
}

fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val ym = YearMonth.of(year, month)
    val fromDay = ym.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    val toDay = ym.atEndOfMonth().atStartOfDay(zone).plusDays(1).toInstant().toEpochMilli() - 1
    return fromDay to toDay
}

fun Duration.toTimerText(): String {
    val totalMinutes = inWholeMinutes
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return if (hours > 0) {
        "%02dh:%02dm".format(hours, minutes)
    } else {
        "%02dm".format(minutes)
    }
}
