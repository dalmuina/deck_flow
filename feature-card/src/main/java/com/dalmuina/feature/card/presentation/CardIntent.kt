package com.dalmuina.feature.card.presentation

import com.dalmuina.feature.card.model.SwipeDirection

sealed interface CardIntent {

    data class SwipeTopCard(val direction : SwipeDirection, val total: Long): CardIntent
}
