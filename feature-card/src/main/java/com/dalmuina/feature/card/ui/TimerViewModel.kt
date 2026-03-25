package com.dalmuina.feature.card.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.usecase.ObserveTimerStateUseCase
import com.dalmuina.domain.usecase.SaveTimerStateUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(
    private val observeTimerStateUseCase: ObserveTimerStateUseCase,
    private val saveTimerStateUseCase: SaveTimerStateUseCase,
) : ViewModel() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var endTime: Long? = null

    init {
        restoreTimer()
    }

    fun process(intent: TimerIntent) {
        when (intent) {
            is TimerIntent.Start -> start(intent.durationMillis)
            TimerIntent.Resume -> resume()
            TimerIntent.Pause -> pause()
            is TimerIntent.Reset -> reset(intent.durationMillis)
        }
    }

    private fun restoreTimer() {
        viewModelScope.launch {
            val persisted = observeTimerStateUseCase().firstOrNull() ?: return@launch
            val now = System.currentTimeMillis()
            val persistedEndTime = persisted.endTimeMillis

            if (persisted.isRunning && persistedEndTime != null) {
                val remaining = (persistedEndTime - now).coerceAtLeast(0L)
                if (remaining == 0L) {
                    _timerState.value = TimerState(
                        totalMillis = persisted.totalMillis,
                        remainingMillis = 0L,
                        isRunning = false
                    )
                    endTime = null

                    savePersistedState(
                        totalMillis = persisted.totalMillis,
                        remainingMillis = 0L,
                        isRunning = false,
                        endTimeMillis = null
                    )
                } else {
                    _timerState.value = TimerState(
                        totalMillis = persisted.totalMillis,
                        remainingMillis = remaining,
                        isRunning = true
                    )
                    endTime = persistedEndTime
                    startTicker()
                }
            } else {
                _timerState.value = TimerState(
                    totalMillis = persisted.totalMillis,
                    remainingMillis = persisted.remainingMillis,
                    isRunning = false
                )
                endTime = null
            }
        }
    }

    private fun start(durationMillis: Long) {
        timerJob?.cancel()
        val now = System.currentTimeMillis()
        endTime = now + durationMillis

        reduce {
            copy(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = true
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = true,
                endTimeMillis = endTime
            )
        }

        startTicker()
    }

    private fun resume() {
        if (_timerState.value.isRunning) return

        val remaining = _timerState.value.remainingMillis
        if (remaining <= 0L) return

        timerJob?.cancel()

        val now = System.currentTimeMillis()
        endTime = now + remaining

        reduce {
            copy(isRunning = true)
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = _timerState.value.totalMillis,
                remainingMillis = remaining,
                isRunning = true,
                endTimeMillis = endTime
            )
        }

        startTicker()
    }

    private fun pause() {
        timerJob?.cancel()
        timerJob = null

        val remaining = endTime
            ?.let { it - System.currentTimeMillis() }
            ?.coerceAtLeast(0L)
            ?: _timerState.value.remainingMillis

        endTime = null

        reduce {
            copy(
                remainingMillis = remaining,
                isRunning = false
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = _timerState.value.totalMillis,
                remainingMillis = remaining,
                isRunning = false,
                endTimeMillis = null
            )
        }
    }

    private fun reset(durationMillis: Long) {
        timerJob?.cancel()
        timerJob = null
        endTime = null

        reduce {
            copy(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = false
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = false,
                endTimeMillis = null
            )
        }
    }

    private fun startTicker() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (true) {
                val currentEndTime = endTime ?: break
                val remaining = (currentEndTime - System.currentTimeMillis()).coerceAtLeast(0L)
                if (remaining == 0L) {
                    reduce {
                        copy(
                            remainingMillis = 0L,
                            isRunning = false
                        )
                    }

                    endTime = null

                    savePersistedState(
                        totalMillis = _timerState.value.totalMillis,
                        remainingMillis = 0L,
                        isRunning = false,
                        endTimeMillis = null
                    )
                    break
                }

                reduce {
                    copy(
                        remainingMillis = remaining,
                        isRunning = true
                    )
                }

                delay(1000)
            }
        }
    }

    private suspend fun savePersistedState(
        totalMillis: Long,
        remainingMillis: Long,
        isRunning: Boolean,
        endTimeMillis: Long?,
    ) {
        saveTimerStateUseCase(
            PersistedTimerState(
                totalMillis = totalMillis,
                remainingMillis = remainingMillis,
                isRunning = isRunning,
                endTimeMillis = endTimeMillis
            )
        )
    }

    private inline fun reduce(
        reducer: TimerState.() -> TimerState
    ) {
        _timerState.update { it.reducer() }
    }
}
