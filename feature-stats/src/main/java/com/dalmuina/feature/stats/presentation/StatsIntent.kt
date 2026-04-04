package com.dalmuina.feature.stats.presentation


sealed interface StatsIntent {
    data class SelectCard(val cardId: Int): StatsIntent
    data class SelectDeck(val deckId: Int): StatsIntent
}
