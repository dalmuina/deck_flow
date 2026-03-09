package com.dalmuina.data.repository

import com.dalmuina.data.datasource.LocalDeckDataSource
import com.dalmuina.data.entity.toDomain
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeckSummary
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.model.DeckForEdit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class LocalDeckRepositoryImpl(
    private val dataSource: LocalDeckDataSource,
) : LocalDeckRepository {

    override suspend fun createDeck(
        name: String,
        cardIds: Set<Int>
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

    override suspend fun addCardToDeck(
        deckId: Int,
        cardId: Int
    ): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.addCardToDeck(deckId,cardId)
        }
    }

    override suspend fun removeCardFromDeck(
        deckId: Int,
        cardId: Int
    ): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.removeCardFromDeck(deckId,cardId)
        }
    }

    override fun getAllDecks(): Flow<DFResult<List<DFDeckSummary>, DataBaseError>> {
        return dataSource
            .getDeckSummaries()
            .map { entities ->
                DFResult.Success(
                    entities.map { it.toDomain() }
                ) as DFResult<List<DFDeckSummary>, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

    override fun getDeckForEdit(deckId: Int): Flow<DFResult<DeckForEdit, DataBaseError>> {
        return dataSource
            .getDeckWithCards(deckId)
            .map { deck ->
                DFResult.Success(
                    DeckForEdit(
                        name = deck.deck.name,
                        cardIds = deck.cards.map { it.id }
                    )
                ) as DFResult<DeckForEdit, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

    override suspend fun deleteDeck(deckId: Int) : DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.deleteDeck(deckId)
        }
    }
}