package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable
import kotlin.time.Duration

@Immutable
data class CardUi(
    val id: String,
    val title: String,
    val duration: Duration,
    val isChecked: Boolean,
)

fun Duration.toTimerText(): String {
    val totalSeconds = inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("%02d:".format(hours))
        append("%02d:%02d".format(minutes, seconds))
    }
}
