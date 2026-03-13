package com.dalmuina.feature.card.ui

sealed interface CardIntent {
    data object CompleteTopCard: CardIntent
}