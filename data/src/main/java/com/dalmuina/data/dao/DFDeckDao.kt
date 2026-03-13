package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dalmuina.data.entity.DFDeckCardCrossRef
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckWithCards
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: DFDeckCardCrossRef)

    @Query(
        """
        DELETE FROM deck_card_cross_ref
        WHERE deckId = :deckId AND cardId = :cardId
    """
    )
    suspend fun deleteCrossRef(deckId: Int, cardId: Int)

    @Transaction
    @Query("SELECT * FROM decks")
    fun getAllDecksWithCards(): Flow<List<DFDeckWithCards>>

    @Transaction
    @Query(
        """
        SELECT * FROM decks WHERE id = :deckId
    """
    )
    fun getDeckById(deckId: Int): Flow<DFDeckEntity>

    @Transaction
    @Query(
        """
        SELECT * FROM decks WHERE id = :deckId
    """
    )
    fun getCardIdsForDeck(deckId: Int): Flow<DFDeckWithCards>

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

    @Query(
        """
    DELETE FROM decks
    WHERE id = :deckId
"""
    )
    suspend fun deleteDeck(deckId: Int)

}
