package com.dalmuina.data.datasource

import android.database.sqlite.SQLiteConstraintException
import app.cash.turbine.test
import com.dalmuina.core.test.helpers.FakeCrashlyticsLogger
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.CardInDeckEntity
import com.dalmuina.data.entity.CardWithProgress
import com.dalmuina.data.entity.CardEntity
import com.dalmuina.data.entity.DeckEntity
import com.dalmuina.data.entity.DeckWithCards
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.DeckDomain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomDeckDataSourceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao: DFDeckDao = mockk()
    private val logger = FakeCrashlyticsLogger()
    private lateinit var datasource: RoomDeckDataSource

    @Before
    fun setUp() {
        datasource = RoomDeckDataSource(dao,logger)
    }

    // region createDeck

    @Test
    fun `createDeck returns Success on happy path`() = runTest {
        coJustRun { dao.insertDeckWithCards(any(), any()) }

        val result = datasource.createDeck(name = "My Deck", cardIds = listOf(1, 2, 3))

        result shouldBe DFResult.Success(Unit)
    }

    @Test
    fun `createDeck returns ConstraintViolation when dao throws SQLiteConstraintException`() = runTest {
        coEvery { dao.insertDeckWithCards(any(), any()) } throws SQLiteConstraintException()

        val result = datasource.createDeck(name = "My Deck", cardIds = listOf(1, 2))

        result shouldBe DFResult.Error(DataError.Local.ConstraintViolation)
    }

    @Test
    fun `createDeck returns Unknown error when dao throws RuntimeException`() = runTest {
        coEvery { dao.insertDeckWithCards(any(), any()) } throws RuntimeException("DB error")

        val result = datasource.createDeck(name = "My Deck", cardIds = listOf(1, 2))

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region updateDeckName

    @Test
    fun `updateDeckName returns Success on happy path`() = runTest {
        coJustRun { dao.updateDeckName(any(), any()) }

        val result = datasource.updateDeckName(deckId = 1, name = "New Name")

        result shouldBe DFResult.Success(Unit)
    }

    @Test
    fun `updateDeckName returns Unknown error when dao throws`() = runTest {
        coEvery { dao.updateDeckName(any(), any()) } throws RuntimeException("DB error")

        val result = datasource.updateDeckName(deckId = 1, name = "New Name")

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region getAllDecksWithCards

    @Test
    fun `getAllDecksWithCards emits Success with mapped domains on happy path`() = runTest {
        val deckEntity = DeckEntity(id = 1, name = "Deck 1")
        val deckWithCards = DeckWithCards(deck = deckEntity, cards = emptyList())
        val expectedDomain = DeckDomain(id = 1, name = "Deck 1", cards = emptyList())
        every { dao.getAllDecksWithCards() } returns flowOf(listOf(deckWithCards))

        datasource.getAllDecksWithCards().test {
            awaitItem() shouldBe DFResult.Success(listOf(expectedDomain))
            awaitComplete()
        }
    }

    @Test
    fun `getAllDecksWithCards emits Success with empty list when no decks exist`() = runTest {
        every { dao.getAllDecksWithCards() } returns flowOf(emptyList())

        datasource.getAllDecksWithCards().test {
            awaitItem() shouldBe DFResult.Success(emptyList<DeckDomain>())
            awaitComplete()
        }
    }

    @Test
    fun `getAllDecksWithCards emits Unknown error when flow throws`() = runTest {
        every { dao.getAllDecksWithCards() } returns flow { throw RuntimeException("DB error") }

        datasource.getAllDecksWithCards().test {
            awaitItem().shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
            awaitComplete()
        }
    }

    // endregion

    // region getDeckWithCardsById

    @Test
    fun `getDeckWithCardsById emits Success with mapped deck on happy path`() = runTest {
        val deckEntity = DeckEntity(id = 2, name = "Deck 2")
        val cardInDeck = CardInDeckEntity(
            id = 1, name = "Card 1", duration = 15000L,
            completedAt = null, postponedAt = null, order = 0
        )
        every { dao.getDeckById(2) } returns flowOf(deckEntity)
        every { dao.getCardsForDeck(2) } returns flowOf(listOf(cardInDeck))

        datasource.getDeckWithCardsById(2).test {
            val item = awaitItem() as DFResult.Success
            item.data.id shouldBe 2
            item.data.name shouldBe "Deck 2"
            item.data.cards.size shouldBe 1
            awaitComplete()
        }
    }

    @Test
    fun `getDeckWithCardsById emits Unknown error when getDeckById flow throws`() = runTest {
        every { dao.getDeckById(any()) } returns flow { throw RuntimeException("DB error") }
        every { dao.getCardsForDeck(any()) } returns flowOf(emptyList())

        datasource.getDeckWithCardsById(99).test {
            awaitItem().shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
            awaitComplete()
        }
    }

    // endregion

    // region deleteDeck

    @Test
    fun `deleteDeck returns Success on happy path`() = runTest {
        coJustRun { dao.deleteDeck(any()) }

        val result = datasource.deleteDeck(1)

        result shouldBe DFResult.Success(Unit)
    }

    @Test
    fun `deleteDeck returns Unknown error when dao throws`() = runTest {
        coEvery { dao.deleteDeck(any()) } throws RuntimeException("DB error")

        val result = datasource.deleteDeck(1)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region setDeckCards

    @Test
    fun `setDeckCards returns Success on happy path`() = runTest {
        coJustRun { dao.replaceDeckCards(any(), any()) }

        val result = datasource.setDeckCards(deckId = 1, orderedIds = listOf(3, 1, 2))

        result shouldBe DFResult.Success(Unit)
    }

    @Test
    fun `setDeckCards returns Unknown error when dao throws`() = runTest {
        coEvery { dao.replaceDeckCards(any(), any()) } throws RuntimeException("DB error")

        val result = datasource.setDeckCards(deckId = 1, orderedIds = listOf(3, 1, 2))

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion
}
