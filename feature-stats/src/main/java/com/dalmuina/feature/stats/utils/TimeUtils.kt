package com.dalmuina.feature.stats.utils

import java.util.Calendar

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