package com.dalmuina.feature.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var endTime: Long? = null

    fun start(durationMillis: Long? = null) {

        if (_timerState.value.isRunning) return

        val now = System.currentTimeMillis()

        endTime = when {
            durationMillis != null -> now + durationMillis
            endTime != null -> endTime
            else -> now + _timerState.value.remainingMillis
        }

        timerJob = viewModelScope.launch {

            while (true) {

                val remaining = endTime!! - System.currentTimeMillis()

                if (remaining <= 0) {
                    _timerState.value = TimerState(
                        totalMillis = _timerState.value.totalMillis,
                        remainingMillis = 0,
                        isRunning = false
                    )
                    break
                }

                _timerState.value = TimerState(
                    totalMillis = _timerState.value.totalMillis,
                    remainingMillis = remaining,
                    isRunning = true
                )

                delay(1000)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null

        val remaining = endTime?.let { it - System.currentTimeMillis() }
            ?: _timerState.value.remainingMillis

        endTime = null

        _timerState.update {
            it.copy(
                remainingMillis = remaining.coerceAtLeast(0),
                isRunning = false
            )
        }
    }

    fun reset(durationMillis: Long) {
        timerJob?.cancel()
        endTime = null

        _timerState.value = TimerState(
            totalMillis = durationMillis,
            remainingMillis = durationMillis,
            isRunning = false
        )
    }
}
