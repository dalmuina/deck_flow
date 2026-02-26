package com.dalmuina.data.repository

import com.dalmuina.data.datasource.LocalCardDataSource
import com.dalmuina.data.entity.toDomain
import com.dalmuina.data.entity.toEntity
import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class LocalCardRepositoryImpl(
    private val dataSource: LocalCardDataSource,
) : LocalCardRepository {

    override suspend fun saveCard(card: DFCard): DFResult<Unit, DataBaseError> {
        return safeDbCall {
            dataSource.save(card.toEntity())
        }
    }

    override fun getAllCards(): Flow<DFResult<List<DFCard>, DataBaseError>> {
        return dataSource
            .getAllCards()
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<DFCard>, DataBaseError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataBaseError.Unknown(e)))
            }
    }

}