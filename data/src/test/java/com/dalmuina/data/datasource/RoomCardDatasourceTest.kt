package com.dalmuina.data.datasource

import android.database.sqlite.SQLiteConstraintException
import app.cash.turbine.test
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.helpers.FakeCrashlyticsLogger
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.CardEntity
import com.dalmuina.data.entity.DailyStatsEntity
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.DailyStatsDomain
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
class RoomCardDatasourceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dao: DFCardDao = mockk()
    private val logger = FakeCrashlyticsLogger()
    private lateinit var datasource: RoomCardDatasource

    @Before
    fun setUp() {
        datasource = RoomCardDatasource(dao,logger)
    }

    // region saveCard

    @Test
    fun `saveCard returns Success with inserted id on happy path`() = runTest {
        val card = CardDomainTestData.card(id = 0)
        coEvery { dao.insert(any()) } returns 5L

        val result = datasource.saveCard(card)

        result shouldBe DFResult.Success(5)
    }

    @Test
    fun `saveCard returns ConstraintViolation when dao throws SQLiteConstraintException`() = runTest {
        val card = CardDomainTestData.card(id = 0)
        coEvery { dao.insert(any()) } throws SQLiteConstraintException()

        val result = datasource.saveCard(card)

        result shouldBe DFResult.Error(DataError.Local.ConstraintViolation)
    }

    @Test
    fun `saveCard returns Unknown error when dao throws RuntimeException`() = runTest {
        val card = CardDomainTestData.card(id = 0)
        coEvery { dao.insert(any()) } throws RuntimeException("DB error")

        val result = datasource.saveCard(card)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region updateCard

    @Test
    fun `updateCard returns Success with card id on happy path`() = runTest {
        val card = CardDomainTestData.card(id = 3)
        coEvery { dao.update(any()) } returns 1

        val result = datasource.updateCard(card)

        result shouldBe DFResult.Success(3)
    }

    @Test
    fun `updateCard returns Unknown error when dao throws`() = runTest {
        val card = CardDomainTestData.card(id = 3)
        coEvery { dao.update(any()) } throws RuntimeException("DB error")

        val result = datasource.updateCard(card)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region completeCard

    @Test
    fun `completeCard returns Success with cardId on happy path`() = runTest {
        coJustRun { dao.insertProgress(any()) }
        coJustRun { dao.markCompleted(any(), any()) }
        coEvery { dao.insertCompletedStat(any()) } returns 1L

        val result = datasource.completeCard(cardId = 7, spentMillis = 5000L)

        result shouldBe DFResult.Success(7)
    }

    @Test
    fun `completeCard returns Unknown error when insertProgress throws`() = runTest {
        coEvery { dao.insertProgress(any()) } throws RuntimeException("DB error")

        val result = datasource.completeCard(cardId = 7, spentMillis = 5000L)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region postponeCard

    @Test
    fun `postponeCard returns Success with cardId on happy path`() = runTest {
        coJustRun { dao.insertProgress(any()) }
        coJustRun { dao.markPostponed(any(), any()) }

        val result = datasource.postponeCard(cardId = 4)

        result shouldBe DFResult.Success(4)
    }

    @Test
    fun `postponeCard returns Unknown error when insertProgress throws`() = runTest {
        coEvery { dao.insertProgress(any()) } throws RuntimeException("DB error")

        val result = datasource.postponeCard(cardId = 4)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region getAllCards

    @Test
    fun `getAllCards emits Success with mapped domains on happy path`() = runTest {
        val entity = CardEntity(id = 1, name = "Card 1", duration = 15000L)
        val expectedDomain = CardDomain(id = 1, name = "Card 1", durationMillis = 15000L)
        every { dao.getAllCards() } returns flowOf(listOf(entity))

        datasource.getAllCards().test {
            awaitItem() shouldBe DFResult.Success(listOf(expectedDomain))
            awaitComplete()
        }
    }

    @Test
    fun `getAllCards emits Success with empty list when no cards exist`() = runTest {
        every { dao.getAllCards() } returns flowOf(emptyList())

        datasource.getAllCards().test {
            awaitItem() shouldBe DFResult.Success(emptyList<CardDomain>())
            awaitComplete()
        }
    }

    @Test
    fun `getAllCards emits Unknown error when flow throws`() = runTest {
        every { dao.getAllCards() } returns flow { throw RuntimeException("DB error") }

        datasource.getAllCards().test {
            awaitItem().shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
            awaitComplete()
        }
    }

    // endregion

    // region getCardById

    @Test
    fun `getCardById returns Success with mapped domain on happy path`() = runTest {
        val entity = CardEntity(id = 1, name = "Card 1", duration = 15000L)
        coEvery { dao.getCardById(1) } returns entity

        val result = datasource.getCardById(1)

        result shouldBe DFResult.Success(CardDomain(id = 1, name = "Card 1", durationMillis = 15000L))
    }

    @Test
    fun `getCardById returns Unknown error when dao throws`() = runTest {
        coEvery { dao.getCardById(any()) } throws RuntimeException("DB error")

        val result = datasource.getCardById(99)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region deleteCard

    @Test
    fun `deleteCard returns Success on happy path`() = runTest {
        coJustRun { dao.deleteCard(any()) }

        val result = datasource.deleteCard(1)

        result shouldBe DFResult.Success(Unit)
    }

    @Test
    fun `deleteCard returns Unknown error when dao throws`() = runTest {
        coEvery { dao.deleteCard(any()) } throws RuntimeException("DB error")

        val result = datasource.deleteCard(1)

        result.shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
    }

    // endregion

    // region getDailyStatsForCard

    @Test
    fun `getDailyStatsForCard emits Success with mapped stats on happy path`() = runTest {
        val statsEntity = DailyStatsEntity(dayStart = 1000L, totalSpentMillis = 5000L, completedCount = 2)
        val expectedDomain = DailyStatsDomain(dayStart = 1000L, totalSpentMillis = 5000L, completedCount = 2)
        every { dao.getDailyStatsForCard(any(), any(), any()) } returns flowOf(listOf(statsEntity))

        datasource.getDailyStatsForCard(cardId = 1, fromDay = 0L, toDay = 2000L).test {
            awaitItem() shouldBe DFResult.Success(listOf(expectedDomain))
            awaitComplete()
        }
    }

    @Test
    fun `getDailyStatsForCard emits Success with empty list when no stats`() = runTest {
        every { dao.getDailyStatsForCard(any(), any(), any()) } returns flowOf(emptyList())

        datasource.getDailyStatsForCard(cardId = 1, fromDay = 0L, toDay = 2000L).test {
            awaitItem() shouldBe DFResult.Success(emptyList<DailyStatsDomain>())
            awaitComplete()
        }
    }

    @Test
    fun `getDailyStatsForCard emits Unknown error when flow throws`() = runTest {
        every { dao.getDailyStatsForCard(any(), any(), any()) } returns flow { throw RuntimeException("DB error") }

        datasource.getDailyStatsForCard(cardId = 1, fromDay = 0L, toDay = 2000L).test {
            awaitItem().shouldBeInstanceOf<DFResult.Error<DataError.Local.Unknown>>()
            awaitComplete()
        }
    }

    // endregion
}
