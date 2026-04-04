package com.dalmuina.domain.model

data class PersistedTimerState(
    val totalMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val isRunning: Boolean = false,
    val endTimeMillis: Long? = null,
)
