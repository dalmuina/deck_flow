package com.dalmuina.feature.card.ui

sealed interface TimerIntent {
    data class Play(val durationMillis: Long? = null) : TimerIntent
    data object Stop : TimerIntent
    data class Reset(val durationMillis: Long) : TimerIntent
}