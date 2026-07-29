package com.dalmuina.domain.model

data class PersistedTimerState(
    val totalMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val isRunning: Boolean = false,
    val endTimeMillis: Long? = null,
    val elapsedMillis: Long = 0L,
    val cardId: Int? = null,
)

fun PersistedTimerState.pausedAt(now: Long): PersistedTimerState {
    val currentEndTime = endTimeMillis
    val elapsed = currentEndTime?.let { (now - (it - totalMillis)).coerceAtLeast(0L) } ?: elapsedMillis
    val remaining = currentEndTime?.let { (it - now).coerceAtLeast(0L) } ?: remainingMillis

    return copy(
        remainingMillis = remaining,
        elapsedMillis = elapsed,
        isRunning = false,
        endTimeMillis = null,
    )
}

fun PersistedTimerState.resumedAt(now: Long): PersistedTimerState {
    if (isRunning || totalMillis == 0L) return this

    val newEndTime = now - elapsedMillis + totalMillis

    return copy(
        isRunning = true,
        endTimeMillis = newEndTime,
    )
}
