package com.dalmuina.deckflow.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Card : Route

    @Serializable
    data object DeckSelector : Route

    @Serializable
    data object DeckCreator : Route
}