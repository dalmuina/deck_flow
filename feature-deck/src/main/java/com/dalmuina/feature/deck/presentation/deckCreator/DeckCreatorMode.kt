package com.dalmuina.feature.deck.presentation.deckCreator

import kotlinx.serialization.Serializable

@Serializable
sealed interface DeckCreatorMode {
    val deckId: Int?

    @Serializable
    data object Create : DeckCreatorMode {
        override val deckId: Int? = null
    }

    @Serializable
    data class Edit(override val deckId: Int) : DeckCreatorMode
}
