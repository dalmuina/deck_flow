package com.dalmuina.domain.usecase

import android.util.Log
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.model.map
import com.dalmuina.core.utils.isToday
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

class GetDeckByIdUseCase(
    private val repository: LocalDeckRepository,
    private val clock: Clock,
) {

    operator fun invoke(deckId: Int): Flow<DFResult<DFDeck, DataBaseError>> =
        repository.getDeckWithCardsById(deckId)
            .map { result ->
                result.map { deck ->
                    normalizeDeck(deck)
                }
            }

    private fun normalizeDeck(deck: DFDeck): DFDeck {

        val now = clock.millis()

        return deck.copy(
            cards = deck.cards.map { card ->

                val completedToday =
                    card.completedAt?.let { isToday(it, now) } == true

                val postponedToday =
                    card.postponedAt?.let { isToday(it, now) } == true

                card.copy(
                    completedAt = if (completedToday) card.completedAt else null,
                    postponedAt = if (postponedToday) card.postponedAt else null
                )
            }
        )
    }
}