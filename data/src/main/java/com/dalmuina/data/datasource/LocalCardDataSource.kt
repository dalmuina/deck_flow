package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.DFCardEntity
import kotlinx.coroutines.flow.Flow

class LocalCardDataSource(
    private val dao: DFCardDao
) {
    suspend fun save(card: DFCardEntity) {
        dao.insert(card)
    }

    fun getAllCards(): Flow<List<DFCardEntity>> {
        return dao.getAllCards()
    }
}