package com.dalmuina.data.repository

import com.dalmuina.data.datasource.LocalCardDataSource
import com.dalmuina.data.entity.toDomain
import com.dalmuina.data.entity.toEntity
import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class LocalCardRepositoryImpl(
    private val dataSource: LocalCardDataSource,
) : LocalCardRepository {
    override suspend fun saveCard(card: DFCardDomain): DFResult<Int, DataBaseError> {
        return safeDbCall {
            dataSource.saveCard(card.toEntity()).toInt()
        }
    }

    override suspend fun updateCard(card: DFCardDomain): DFResult<Int, DataBaseError> {
        return safeDbCall {
            dataSource.updateCard(card.toEntity())
            card.id
        }
    }

    override suspend fun completeCard(cardId: Int, spentMillis: Long): DFResult<Int, DataBaseError> {
        val now = System.currentTimeMillis()

        return safeDbCall {
            dataSource.completeCard(cardId, now, spentMillis)
            cardId
        }
    }

    override suspend fun postponeCard(cardId: Int): DFResult<Int, DataBaseError> {
        val now = System.currentTimeMillis()

        return safeDbCall {
            dataSource.postponeCard(cardId, now)
            cardId
        }
    }

    override fun getAllCards(): Flow<DFResult<List<DFCardDomain>, DataBaseError>> {
        return dataSource
            .getAllCards()
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<DFCardDomain>, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

    override suspend fun getCardById(cardId: Int): DFResult<DFCardDomain, DataBaseError> {
        return safeDbCall {
            dataSource.getCardByID(cardId).toDomain()
        }
    }

    override suspend fun deleteCard(cardId: Int): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.deleteCard(cardId)
        }
    }

}