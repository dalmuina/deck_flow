package com.dalmuina.feature.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var endTime: Long? = null

    fun process(intent: TimerIntent) {
        when (intent) {
            is TimerIntent.Play -> play(intent.durationMillis)
            is TimerIntent.Reset -> reset(intent.durationMillis)
            TimerIntent.Stop -> stop()
        }
    }

    private fun play(durationMillis: Long? = null) {

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
                    reduce {
                        this.copy(
                            totalMillis = _timerState.value.totalMillis,
                            remainingMillis = 0,
                            isRunning = false
                        )
                    }
                    break
                }

                reduce {
                    this.copy(
                        totalMillis = _timerState.value.totalMillis,
                        remainingMillis = remaining,
                        isRunning = true
                    )
                }

                delay(1000)
            }
        }
    }

    private fun stop() {
        timerJob?.cancel()
        timerJob = null

        val remaining = endTime?.let { it - System.currentTimeMillis() }
            ?: _timerState.value.remainingMillis

        endTime = null

        reduce {
            this.copy(
                remainingMillis = remaining.coerceAtLeast(0),
                isRunning = false
            )
        }
    }

    private fun reset(durationMillis: Long) {
        timerJob?.cancel()
        endTime = null

        reduce {
            this.copy(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = false
            )
        }
    }

    private inline fun reduce(
        reducer: TimerState.() -> TimerState
    ) {
        _timerState.update {
            it.reducer()
        }
    }
}
