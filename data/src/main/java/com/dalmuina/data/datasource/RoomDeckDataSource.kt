package com.dalmuina.data.datasource

import com.dalmuina.core.data.helpers.CrashlyticsLogger
import com.dalmuina.core.data.helpers.safeDbCall
import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DeckEntity
import com.dalmuina.data.entity.toDomain
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.DeckDomain
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.asEmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class RoomDeckDataSource(
    private val dao: DFDeckDao,
    private val logger: CrashlyticsLogger,
) : DeckLocalDataSource {
    override suspend fun createDeck(
        name: String,
        cardIds: List<Int>,
    ): DFResult<Int, DataError> =
        safeDbCall(logger) {
            dao.insertDeckWithCards(
                deck = DeckEntity(name = name),
                cardIds = cardIds,
            )
        }

    override suspend fun updateDeckName(
        deckId: Int,
        name: String,
    ): EmptyResult<DataError> =
        safeDbCall(logger) {
            dao.updateDeckName(deckId, name)
        }.asEmptyResult()

    override fun getAllDecksWithCards(): Flow<DFResult<List<DeckDomain>, DataError>> =
        dao
            .getAllDecksWithCards()
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                    as DFResult<List<DeckDomain>, DataError>
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataError.Local.Unknown(e)))
            }

    override fun getDeckWithCardsById(deckId: Int): Flow<DFResult<DeckDomain, DataError>> =
        combine(
            dao.getDeckById(deckId),
            dao.getCardsForDeck(deckId),
        ) { deck, cards ->
            DFResult.Success(
                DeckDomain(
                    id = deck.id,
                    name = deck.name,
                    cards = cards.map { it.toDomain() },
                ),
            ) as DFResult<DeckDomain, DataError>
        }.catch { e ->
            if (e is CancellationException) throw e
            emit(DFResult.Error(DataError.Local.Unknown(e)))
        }

    override suspend fun deleteDeck(deckId: Int): EmptyResult<DataError> =
        safeDbCall(logger) {
            dao.deleteDeck(deckId)
        }.asEmptyResult()

    override suspend fun setDeckCards(
        deckId: Int,
        orderedIds: List<Int>,
    ): EmptyResult<DataError> =
        safeDbCall(logger) {
            dao.replaceDeckCards(deckId, orderedIds)
        }.asEmptyResult()
}
