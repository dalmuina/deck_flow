package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DFCard
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class DFCardUi(
    val id: Int=0,
    val title: String,
    val duration: Duration,
    val isSelected: Boolean,
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
    title = title,
    duration = durationMillis.milliseconds,
    isSelected = true,
)
