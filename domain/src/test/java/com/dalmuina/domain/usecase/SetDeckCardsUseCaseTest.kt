package com.dalmuina.domain.usecase

import com.dalmuina.coretest.rules.MainDispatcherRule
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetDeckCardsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocalDeckRepository = mockk()

    @Test
    fun `invoke should return success when repository sets deck cards successfully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = SetDeckCardsUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val deckId = 1
        val orderedIds = listOf(3, 1, 2)
        val expected = DFResult.Success(Unit)

        coEvery { repository.setDeckCards(deckId, orderedIds) } returns expected

        val result = useCase(deckId, orderedIds)

        result shouldBe expected
        coVerify(exactly = 1) { repository.setDeckCards(deckId, orderedIds) }
    }

    @Test
    fun `invoke should return error when repository fails setting deck cards`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = SetDeckCardsUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val deckId = 1
        val orderedIds = listOf(3, 1, 2)
        val expected = DFResult.Error(DataBaseError.ConstraintViolation)

        coEvery { repository.setDeckCards(deckId, orderedIds) } returns expected

        val result = useCase(deckId, orderedIds)

        result shouldBe expected
        coVerify(exactly = 1) { repository.setDeckCards(deckId, orderedIds) }
    }
}