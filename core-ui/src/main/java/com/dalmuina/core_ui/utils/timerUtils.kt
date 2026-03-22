package com.dalmuina.core_ui.utils

import kotlin.time.Duration

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