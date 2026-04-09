package com.dalmuina.deckflow.navigation

import androidx.navigation3.runtime.NavKey
import com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorMode
import com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorMode
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Card : Route

    @Serializable
    data object DeckSelector : Route

    @Serializable
    data class DeckCreator(val mode: DeckCreatorMode) : Route

    @Serializable
    data class CardCreator(val mode: CardCreatorMode) : Route

    @Serializable
    data object Stats: Route
}

fun Route.title(): String {
    return when (this) {
        Route.DeckSelector -> "Decks"
        Route.Card -> "Cards"
        is Route.DeckCreator -> "Create Deck"
        is Route.CardCreator -> "Create Card"
        Route.Stats -> "Stats"
    }
}