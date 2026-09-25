package com.dalmuina.feature.card.presentation.timer

sealed interface TimerIntent {
    data class Start(
        val durationMillis: Long,
        val cardId: Int,
    ) : TimerIntent

    data object Resume : TimerIntent

    data object Pause : TimerIntent

    data class Reset(
        val durationMillis: Long,
        val cardId: Int,
    ) : TimerIntent

    data object Sync : TimerIntent
}
