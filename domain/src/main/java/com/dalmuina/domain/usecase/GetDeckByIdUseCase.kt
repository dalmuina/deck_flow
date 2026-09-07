package com.dalmuina.domain.usecase

import com.dalmuina.core.domain.helpers.getMillis
import com.dalmuina.core.domain.helpers.isToday
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.helpers.sortedForSession
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.DeckDomain
import com.dalmuina.domain.model.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

class GetDeckByIdUseCase(
    private val repository: DeckLocalDataSource,
    private val clock: Clock,
) {
    operator fun invoke(deckId: Int): Flow<DFResult<DeckDomain, DataError>> =
        repository
            .getDeckWithCardsById(deckId)
            .map { result ->
                result.map { deck ->
                    normalizeDeck(deck)
                }
            }

    private fun normalizeDeck(deck: DeckDomain): DeckDomain {
        val now = clock.getMillis()

        val normalizedCards =
            deck.cards.map { card ->

                val completedToday =
                    card.completedAt?.let { isToday(it, now) } == true

                val postponedToday =
                    card.postponedAt?.let { isToday(it, now) } == true

                card.copy(
                    completedAt = if (completedToday) card.completedAt else null,
                    postponedAt = if (postponedToday) card.postponedAt else null,
                )
            }

        return deck.copy(
            cards = normalizedCards.sortedForSession(),
        )
    }
}
