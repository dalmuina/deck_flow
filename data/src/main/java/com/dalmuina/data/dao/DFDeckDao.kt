package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dalmuina.data.entity.DFDeckCardCrossRef
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckSummaryEntity
import com.dalmuina.data.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DFDeckDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDeck(deck: DFDeckEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(refs: List<DFDeckCardCrossRef>)

    @Transaction
    suspend fun insertDeckWithCards(
        deck: DFDeckEntity,
        cardIds: Set<Int>
    ) {
        val deckId = insertDeck(deck).toInt()

        val refs = cardIds.map { cardId ->
            DFDeckCardCrossRef(
                deckId = deckId,
                cardId = cardId
            )
        }

        insertCrossRefs(refs)
    }

    @Query(
        """
        DELETE FROM deck_card_cross_ref
        WHERE deckId = :deckId AND cardId = :cardId
    """
    )
    suspend fun deleteCrossRef(deckId: Int, cardId: Int)

    @Query(
        """
        SELECT d.id, d.name, COUNT(r.cardId) as cardCount 
        FROM decks d
        LEFT JOIN deck_card_cross_ref r
            ON d.id = r.deckId
        GROUP BY d.id
    """
    )
    fun getDeckSummaries(): Flow<List<DFDeckSummaryEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM decks WHERE id = :deckId
    """
    )
    fun getCardIdsForDeck(deckId: Int): Flow<DeckWithCards>

    @Query(
        """
    UPDATE decks
    SET name = :name
    WHERE id = :deckId
    """
    )
    suspend fun updateDeckName(deckId: Int, name: String)

    @Query(
        """
    DELETE FROM deck_card_cross_ref
    WHERE deckId = :deckId
    """
    )
    suspend fun deleteCrossRefs(deckId: Int)

    @Transaction
    suspend fun updateDeckWithCards(
        deckId: Int,
        name: String,
        cardIds: Set<Int>
    ) {

        updateDeckName(deckId, name)

        deleteCrossRefs(deckId)

        val refs = cardIds.map { cardId ->
            DFDeckCardCrossRef(
                deckId = deckId,
                cardId = cardId
            )
        }

        insertCrossRefs(refs)
    }
}
