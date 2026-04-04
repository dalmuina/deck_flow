package com.dalmuina.domain.usecase

import app.cash.turbine.test
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.data.DeckDomainTestData
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.helpers.sortedForSession
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class GetDeckByIdUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DeckLocalDataSource = mockk()
    private val clock: Clock = mockk()

    @Test
    fun `invoke should emit error when repository returns error`() = runTest {
        val deckId = 1
        val expected = DFResult.Error(DataBaseError.ConstraintViolation)

        every { repository.getDeckWithCardsById(deckId) } returns flowOf(expected)

        val useCase = GetDeckByIdUseCase(
            repository = repository,
            clock = clock
        )

        useCase(deckId).test {
            awaitItem() shouldBe expected
            awaitComplete()
        }
    }

    @Test
    fun `invoke should keep today timestamps and clear old timestamps`() = runTest {
        val deckId = 1
        val now = Instant.parse("2026-03-21T10:00:00Z").toEpochMilli()
        val todayMillis = Instant.parse("2026-03-21T08:00:00Z").toEpochMilli()
        val oldMillis = Instant.parse("2026-03-20T08:00:00Z").toEpochMilli()

        every { clock.millis() } returns now

        val cardCompletedToday = CardDomainTestData.card(
            id = 1,
            completedAt = todayMillis,
            postponedAt = null
        )
        val cardCompletedOld = CardDomainTestData.card(
            id = 2,
            completedAt = oldMillis,
            postponedAt = null
        )
        val cardPostponedToday = CardDomainTestData.card(
            id = 3,
            completedAt = null,
            postponedAt = todayMillis
        )
        val cardPostponedOld = CardDomainTestData.card(
            id = 4,
            completedAt = null,
            postponedAt = oldMillis
        )

        val deck = DeckDomainTestData.deck(
            id = deckId,
            cards = listOf(
                cardCompletedToday,
                cardCompletedOld,
                cardPostponedToday,
                cardPostponedOld
            )
        )

        every { repository.getDeckWithCardsById(deckId) } returns flowOf(
            DFResult.Success(deck)
        )

        val useCase = GetDeckByIdUseCase(
            repository = repository,
            clock = clock
        )

        useCase(deckId).test {
            val result = awaitItem()

            result shouldBe DFResult.Success(
                deck.copy(
                    cards = listOf(
                        cardCompletedToday.copy(
                            completedAt = todayMillis,
                            postponedAt = null
                        ),
                        cardCompletedOld.copy(
                            completedAt = null,
                            postponedAt = null
                        ),
                        cardPostponedToday.copy(
                            completedAt = null,
                            postponedAt = todayMillis
                        ),
                        cardPostponedOld.copy(
                            completedAt = null,
                            postponedAt = null
                        )
                    ).sortedForSession()
                )
            )

            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit normalized deck with cards sorted for session`() = runTest {
        val deckId = 1
        val now = Instant.parse("2026-03-21T10:00:00Z").toEpochMilli()

        every { clock.millis() } returns now

        val first = CardDomainTestData.card(id = 1)
        val second = CardDomainTestData.card(id = 2)
        val third = CardDomainTestData.card(id = 3)

        val cards = listOf(first, second, third)
        val deck = DeckDomainTestData.deck(
            id = deckId,
            cards = cards
        )

        every { repository.getDeckWithCardsById(deckId) } returns flowOf(
            DFResult.Success(deck)
        )

        val useCase = GetDeckByIdUseCase(
            repository = repository,
            clock = clock
        )

        useCase(deckId).test {
            val result = awaitItem()

            result shouldBe DFResult.Success(
                deck.copy(
                    cards = cards
                        .map { card ->
                            card.copy(
                                completedAt = null,
                                postponedAt = null
                            )
                        }
                        .sortedForSession()
                )
            )

            awaitComplete()
        }
    }
}