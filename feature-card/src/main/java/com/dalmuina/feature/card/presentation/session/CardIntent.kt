package com.dalmuina.feature.card.presentation.session

import com.dalmuina.feature.card.model.SwipeDirection

sealed interface CardIntent {

    data class SwipeTopCard(val direction: SwipeDirection) : CardIntent
    data class RequestCompleteCard(val totalMillis: Long) : CardIntent
    data class ChangeCompletionTime(val value: String) : CardIntent
    data object MoreCompletionTime : CardIntent
    data object LessCompletionTime : CardIntent
    data object ConfirmCompletion : CardIntent
    data object DismissCompletion : CardIntent
}
