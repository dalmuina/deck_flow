package com.dalmuina.feature.card.model

import com.dalmuina.domain.model.DFCard
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class DFCardUi(
    val id: Int,
    val name: String,
    val duration: Duration,
)

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

fun DFCard.toCardUi(): DFCardUi = DFCardUi(
    id = id,
    name = name,
    duration = durationMillis.milliseconds
)
