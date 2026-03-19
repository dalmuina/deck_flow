package com.dalmuina.data.repository

import com.dalmuina.data.datasource.LocalDeckDataSource
import com.dalmuina.data.entity.toDomain
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class LocalDeckRepositoryImpl(
    private val dataSource: LocalDeckDataSource,
) : LocalDeckRepository {

    override suspend fun createDeck(
        name: String,
        cardIds: List<Int>
    ): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.createDeck(name, cardIds)
        }
    }

    override suspend fun updateDeckName(
        deckId: Int,
        name: String
    ): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.updateDeckName(deckId,name)
        }
    }

    override fun getAllDecksWithCards(): Flow<DFResult<List<DFDeckDomain>, DataBaseError>> {
        return dataSource
            .getAllDecksWithCards()
            .map { entities ->
                DFResult.Success(
                    entities.map { it.toDomain() }
                ) as DFResult<List<DFDeckDomain>, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

    override fun getDeckWithCardsById(
        deckId: Int
    ): Flow<DFResult<DFDeckDomain, DataBaseError>> {

        return combine(
            dataSource.getDeck(deckId),
            dataSource.getCardsForDeck(deckId)
        ) { deck, cards ->

            DFResult.Success(
                DFDeckDomain(
                    id = deck.id,
                    name = deck.name,
                    cards = cards.map { it.toDomain() }
                )
            ) as DFResult<DFDeckDomain, DataBaseError>

        }.catch { e ->
            if (e is CancellationException) throw e
            emit(DFResult.Error(DataBaseError.Unknown(e)))
        }
    }

    override suspend fun deleteDeck(deckId: Int) : DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.deleteDeck(deckId)
        }
    }

    override suspend fun setDeckCards(
        deckId: Int,
        orderedIds: List<Int>
    ): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.setDeckCards(deckId, orderedIds)
        }
    }
}