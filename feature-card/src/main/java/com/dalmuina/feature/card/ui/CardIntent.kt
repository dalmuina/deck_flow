package com.dalmuina.feature.card.ui

import com.dalmuina.feature.card.model.SwipeDirection

sealed interface CardIntent {

    data class SwipeTopCard(val direction : SwipeDirection): CardIntent
}