package com.dalmuina.feature.card.presentation

import androidx.compose.runtime.Immutable

data class TimerState(
    val totalMillis: Long = 0,
    val remainingMillis: Long = 0,
    val isRunning: Boolean = false,
)
