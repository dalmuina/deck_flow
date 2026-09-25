package com.dalmuina.deckflow.navigation

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.dalmuina.deckflow.R
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
    data class DeckCreator(
        val mode: DeckCreatorMode,
    ) : Route

    @Serializable
    data class CardCreator(
        val mode: CardCreatorMode,
    ) : Route

    @Serializable
    data object Stats : Route
}

@StringRes
fun Route.titleRes(): Int =
    when (this) {
        Route.DeckSelector -> R.string.nav_decks
        Route.Card -> R.string.nav_title_cards
        is Route.DeckCreator ->
            if (mode is DeckCreatorMode.Edit) {
                R.string.nav_title_deck_update
            } else {
                R.string.nav_title_deck_create
            }
        is Route.CardCreator ->
            if (mode is CardCreatorMode.Edit) {
                R.string.nav_title_card_update
            } else {
                R.string.nav_title_card_create
            }
        Route.Stats -> R.string.nav_stats
    }
