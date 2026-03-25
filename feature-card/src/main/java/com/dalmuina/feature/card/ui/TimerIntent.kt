package com.dalmuina.feature.card.ui

sealed interface TimerIntent {
    data class Start(val durationMillis: Long) : TimerIntent
    data object Resume : TimerIntent
    data object Pause : TimerIntent
    data class Reset(val durationMillis: Long) : TimerIntent
}
