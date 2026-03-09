package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.DFCardEntity
import kotlinx.coroutines.flow.Flow

class LocalCardDataSource(
    private val dao: DFCardDao
) {
    suspend fun saveCard(card: DFCardEntity) =
        dao.insert(card)

    suspend fun updateCard(card: DFCardEntity) =
        dao.update(card)

    fun getAllCards(): Flow<List<DFCardEntity>> =
        dao.getAllCards()

    suspend fun getCardByID(cardId: Int): DFCardEntity =
        dao.getCardById(cardId)

    suspend fun deleteCard(cardId: Int) =
        dao.deleteCard(cardId)
}
