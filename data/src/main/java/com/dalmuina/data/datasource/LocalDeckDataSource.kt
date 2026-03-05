package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckSummaryEntity
import kotlinx.coroutines.flow.Flow

class LocalDeckDataSource(
    private val dao: DFDeckDao
) {

    suspend fun saveDeck(name: String, cardIds: Set<Int>) {
        dao.insertDeckWithCards(
            deck = DFDeckEntity(name = name),
            cardIds = cardIds
        )
    }

    fun getDeckSummaries(): Flow<List<DFDeckSummaryEntity>> {
        return dao.getDeckSummaries()
    }
}
