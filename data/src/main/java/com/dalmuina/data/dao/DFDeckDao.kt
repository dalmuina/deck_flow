package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dalmuina.data.entity.CardInDeckEntity
import com.dalmuina.data.entity.DeckCardCrossEntity
import com.dalmuina.data.entity.DeckEntity
import com.dalmuina.data.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DFDeckDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(refs: List<DeckCardCrossEntity>)

    @Transaction
    suspend fun insertDeckWithCards(
        deck: DeckEntity,
        cardIds: List<Int>,
    ): Int {
        val deckId = insertDeck(deck).toInt()

        val refs =
            cardIds.mapIndexed { index, cardId ->
                DeckCardCrossEntity(
                    deckId = deckId,
                    cardId = cardId,
                    order = index,
                )
            }

        insertCrossRefs(refs)

        return deckId
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: DeckCardCrossEntity)

    @Query(
        """
        DELETE FROM deck_card_cross_ref
        WHERE deckId = :deckId AND cardId = :cardId
    """,
    )
    suspend fun deleteCrossRef(
        deckId: Int,
        cardId: Int,
    )

    @Transaction
    @Query("SELECT * FROM decks")
    fun getAllDecksWithCards(): Flow<List<DeckWithCards>>

    @Query(
        """
    UPDATE decks
    SET name = :name
    WHERE id = :deckId
    """,
    )
    suspend fun updateDeckName(
        deckId: Int,
        name: String,
    )

    @Query(
        """
    DELETE FROM deck_card_cross_ref
    WHERE deckId = :deckId
    """,
    )
    suspend fun deleteCrossRefs(deckId: Int)

    @Transaction
    suspend fun updateDeckWithCards(
        deckId: Int,
        name: String,
        cardIds: Set<Int>,
    ) {
        updateDeckName(deckId, name)

        deleteCrossRefs(deckId)

        val refs =
            cardIds.mapIndexed { index, cardId ->
                DeckCardCrossEntity(
                    deckId = deckId,
                    cardId = cardId,
                    order = index,
                )
            }

        insertCrossRefs(refs)
    }

    @Query(
        """
    DELETE FROM decks
    WHERE id = :deckId
""",
    )
    suspend fun deleteDeck(deckId: Int)

    @Query(
        """
SELECT 
    c.id,
    c.name,
    c.duration,
    p.completedAt,
    p.postponeAt AS postponedAt,
    x.`order`
FROM deck_card_cross_ref x
INNER JOIN cards c ON c.id = x.cardId
LEFT JOIN card_progress p ON p.cardId = c.id
WHERE x.deckId = :deckId
ORDER BY x.`order`
""",
    )
    fun getCardsForDeck(deckId: Int): Flow<List<CardInDeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckById(deckId: Int): Flow<DeckEntity>

    @Transaction
    suspend fun replaceDeckCards(
        deckId: Int,
        cardIds: List<Int>,
    ) {
        deleteCrossRefs(deckId)

        val refs =
            cardIds.mapIndexed { index, cardId ->
                DeckCardCrossEntity(
                    deckId = deckId,
                    cardId = cardId,
                    order = index,
                )
            }

        insertCrossRefs(refs)
    }
}
