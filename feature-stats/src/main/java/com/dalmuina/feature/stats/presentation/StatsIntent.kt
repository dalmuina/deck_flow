package com.dalmuina.feature.stats.presentation

sealed interface StatsIntent {
    data class SelectActivity(val cardId: Int) : StatsIntent
    data object PreviousMonth : StatsIntent
    data object NextMonth : StatsIntent
}