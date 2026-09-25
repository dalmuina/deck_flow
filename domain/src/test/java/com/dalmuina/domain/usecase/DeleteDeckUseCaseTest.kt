package com.dalmuina.domain.usecase

import com.dalmuina.core.test.data.DeckDomainTestData
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteDeckUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DeckLocalDataSource = mockk()
    private val selectedDeckRepository: SelectedDeckDataSource = mockk()

    @Test
    fun `invoke should return error when getting decks fails`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 2
            val expected = DFResult.Error(DataError.Local.ConstraintViolation)

            every { repository.getAllDecksWithCards() } returns flowOf(expected)

            val result = useCase(deckId)

            result shouldBe expected
            coVerify(exactly = 0) { repository.deleteDeck(any()) }
        }

    @Test
    fun `invoke should return null when deleted deck is not selected`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 2
            val decks =
                listOf(
                    DeckDomainTestData.deck(id = 1),
                    DeckDomainTestData.deck(id = 2),
                    DeckDomainTestData.deck(id = 3),
                )

            every { repository.getAllDecksWithCards() } returns flowOf(DFResult.Success(decks))
            every { selectedDeckRepository.selectedDeckId } returns flowOf(DFResult.Success(1))
            coEvery { repository.deleteDeck(deckId) } returns DFResult.Success(Unit)

            val result = useCase(deckId)

            result shouldBe DFResult.Success(null)
            coVerify(exactly = 1) { repository.deleteDeck(deckId) }
        }

    @Test
    fun `invoke should return next deck when deleted selected deck has next`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 2
            val decks =
                listOf(
                    DeckDomainTestData.deck(id = 1),
                    DeckDomainTestData.deck(id = 2),
                    DeckDomainTestData.deck(id = 3),
                )

            every { repository.getAllDecksWithCards() } returns flowOf(DFResult.Success(decks))
            every { selectedDeckRepository.selectedDeckId } returns flowOf(DFResult.Success(2))
            coEvery { repository.deleteDeck(deckId) } returns DFResult.Success(Unit)

            val result = useCase(deckId)

            result shouldBe DFResult.Success(3)
            coVerify(exactly = 1) { repository.deleteDeck(deckId) }
        }

    @Test
    fun `invoke should return previous deck when deleted selected deck has no next`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 3
            val decks =
                listOf(
                    DeckDomainTestData.deck(id = 1),
                    DeckDomainTestData.deck(id = 2),
                    DeckDomainTestData.deck(id = 3),
                )

            every { repository.getAllDecksWithCards() } returns flowOf(DFResult.Success(decks))
            every { selectedDeckRepository.selectedDeckId } returns flowOf(DFResult.Success(3))
            coEvery { repository.deleteDeck(deckId) } returns DFResult.Success(Unit)

            val result = useCase(deckId)

            result shouldBe DFResult.Success(2)
            coVerify(exactly = 1) { repository.deleteDeck(deckId) }
        }

    @Test
    fun `invoke should return null when selected deleted deck is not found in list`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 99
            val decks =
                listOf(
                    DeckDomainTestData.deck(id = 1),
                    DeckDomainTestData.deck(id = 2),
                    DeckDomainTestData.deck(id = 3),
                )

            every { repository.getAllDecksWithCards() } returns flowOf(DFResult.Success(decks))
            every { selectedDeckRepository.selectedDeckId } returns flowOf(DFResult.Success(99))
            coEvery { repository.deleteDeck(deckId) } returns DFResult.Success(Unit)

            val result = useCase(deckId)

            result shouldBe DFResult.Success(null)
            coVerify(exactly = 1) { repository.deleteDeck(deckId) }
        }

    @Test
    fun `invoke should return error when delete deck fails`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                DeleteDeckUseCase(
                    repository = repository,
                    selectedDeckRepository = selectedDeckRepository,
                    dispatcher = dispatcher,
                )

            val deckId = 2
            val decks =
                listOf(
                    DeckDomainTestData.deck(id = 1),
                    DeckDomainTestData.deck(id = 2),
                    DeckDomainTestData.deck(id = 3),
                )
            val expected = DFResult.Error(DataError.Local.ConstraintViolation)

            every { repository.getAllDecksWithCards() } returns flowOf(DFResult.Success(decks))
            every { selectedDeckRepository.selectedDeckId } returns flowOf(DFResult.Success(2))
            coEvery { repository.deleteDeck(deckId) } returns expected

            val result = useCase(deckId)

            result shouldBe expected
            coVerify(exactly = 1) { repository.deleteDeck(deckId) }
        }
}
