package com.dalmuina.feature.deck.ui.cardCreator

import kotlinx.serialization.Serializable

@Serializable
sealed interface CardCreatorMode {
    val cardId: Int?

    @Serializable
    data object Create : CardCreatorMode {
        override val cardId: Int? = null
    }
    @Serializable
    data class Edit(override val cardId: Int) : CardCreatorMode
}
