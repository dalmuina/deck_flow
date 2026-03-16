package com.dalmuina.data.repository

import android.util.Log
import com.dalmuina.data.datasource.LocalDeckDataSource
import com.dalmuina.data.entity.DFDeckWithCards
import com.dalmuina.data.entity.toDomain
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
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

    override fun getAllDecksWithCards(): Flow<DFResult<List<DFDeck>, DataBaseError>> {
        return dataSource
            .getAllDecksWithCards()
            .map { entities ->
                DFResult.Success(
                    entities.map { it.toDomain() }
                ) as DFResult<List<DFDeck>, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

    override fun getDeckWithCardsById (deckId: Int): Flow<DFResult<DFDeck, DataBaseError>> {
        return dataSource
            .getDeckWithCardsById(deckId)
            .map { deckWithCards ->
                DFResult.Success(
                    deckWithCards.toDomain()
                    ) as DFResult<DFDeck, DataBaseError>
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