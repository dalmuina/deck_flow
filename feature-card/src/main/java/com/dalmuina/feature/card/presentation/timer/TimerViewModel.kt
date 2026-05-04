package com.dalmuina.feature.card.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.DFResult
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

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

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
            when (val persistedResult = observeTimerStateUseCase().firstOrNull()) {
                null -> {
                    _isLoaded.value = true
                    return@launch
                }

                is DFResult.Error -> {
                    _isLoaded.value = true
                    return@launch
                }

                is DFResult.Success -> {
                    val persisted = persistedResult.data
                    val now = System.currentTimeMillis()
                    val persistedEndTime = persisted.endTimeMillis

                    if (persisted.isRunning && persistedEndTime != null) {
                        val startTime = persistedEndTime - persisted.totalMillis
                        val elapsed = (now - startTime).coerceAtLeast(0L)
                        val remaining = (persistedEndTime - now).coerceAtLeast(0L)
                        val isOvertime = now >= persistedEndTime

                        _timerState.value = TimerState(
                            totalMillis = persisted.totalMillis,
                            remainingMillis = remaining,
                            elapsedMillis = elapsed,
                            isRunning = true,
                            isOvertime = isOvertime,
                        )
                        endTime = persistedEndTime
                        startTicker()
                    } else {
                        val savedElapsed = persisted.elapsedMillis
                        val elapsed = if (savedElapsed > 0L) {
                            savedElapsed
                        } else {
                            (persisted.totalMillis - persisted.remainingMillis).coerceAtLeast(0L)
                        }
                        _timerState.value = TimerState(
                            totalMillis = persisted.totalMillis,
                            remainingMillis = persisted.remainingMillis,
                            elapsedMillis = elapsed,
                            isRunning = false,
                            isOvertime = persisted.totalMillis in 1..elapsed,
                        )
                        endTime = null
                    }
                    _isLoaded.value = true
                }
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
                elapsedMillis = 0L,
                isRunning = true,
                isOvertime = false,
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = true,
                endTimeMillis = endTime,
                elapsedMillis = 0L,
            )
        }

        startTicker()
    }

    private fun resume() {
        if (_timerState.value.isRunning) return
        if (_timerState.value.totalMillis == 0L) return

        timerJob?.cancel()

        val elapsed = _timerState.value.elapsedMillis
        val now = System.currentTimeMillis()
        // Works for both countdown (elapsed < total) and overtime (elapsed >= total)
        endTime = now - elapsed + _timerState.value.totalMillis

        reduce { copy(isRunning = true) }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = _timerState.value.totalMillis,
                remainingMillis = _timerState.value.remainingMillis,
                isRunning = true,
                endTimeMillis = endTime,
                elapsedMillis = elapsed,
            )
        }

        startTicker()
    }

    private fun pause() {
        timerJob?.cancel()
        timerJob = null

        val now = System.currentTimeMillis()
        val currentEndTime = endTime
        val elapsed = currentEndTime?.let { endT ->
            (now - (endT - _timerState.value.totalMillis)).coerceAtLeast(0L)
        } ?: _timerState.value.elapsedMillis

        val remaining = currentEndTime?.let { (it - now).coerceAtLeast(0L) }
            ?: _timerState.value.remainingMillis

        endTime = null

        reduce {
            copy(
                remainingMillis = remaining,
                elapsedMillis = elapsed,
                isRunning = false,
                isOvertime = elapsed >= totalMillis,
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = _timerState.value.totalMillis,
                remainingMillis = remaining,
                isRunning = false,
                endTimeMillis = null,
                elapsedMillis = elapsed,
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
                elapsedMillis = 0L,
                isRunning = false,
                isOvertime = false,
            )
        }

        viewModelScope.launch {
            savePersistedState(
                totalMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = false,
                endTimeMillis = null,
                elapsedMillis = 0L,
            )
        }
    }

    private fun startTicker() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (true) {
                val currentEndTime = endTime ?: break
                val now = System.currentTimeMillis()
                val elapsed = (now - (currentEndTime - _timerState.value.totalMillis)).coerceAtLeast(0L)
                val remaining = (currentEndTime - now).coerceAtLeast(0L)
                val isOvertime = now >= currentEndTime

                reduce {
                    copy(
                        remainingMillis = remaining,
                        elapsedMillis = elapsed,
                        isRunning = true,
                        isOvertime = isOvertime,
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
        elapsedMillis: Long = 0L,
    ) {
        saveTimerStateUseCase(
            PersistedTimerState(
                totalMillis = totalMillis,
                remainingMillis = remainingMillis,
                isRunning = isRunning,
                endTimeMillis = endTimeMillis,
                elapsedMillis = elapsedMillis,
            )
        )
    }

    private inline fun reduce(
        reducer: TimerState.() -> TimerState
    ) {
        _timerState.update { it.reducer() }
    }
}
