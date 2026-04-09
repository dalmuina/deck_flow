package com.dalmuina.domain.usecase

import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
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
class CreateDeckUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DeckLocalDataSource = mockk()

    @Test
    fun `invoke should return success when repository creates deck successfully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = CreateDeckUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val name = "Morning routine"
        val cardIds = listOf(1, 2, 3)
        val expected = DFResult.Success(Unit)

        coEvery { repository.createDeck(name, cardIds) } returns expected

        val result = useCase(name, cardIds)

        result shouldBe expected
        coVerify(exactly = 1) { repository.createDeck(name, cardIds) }
    }

    @Test
    fun `invoke should return error when repository fails creating deck`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = CreateDeckUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val name = "Morning routine"
        val cardIds = listOf(1, 2, 3)
        val expected = DFResult.Error(DataError.Local.ConstraintViolation)

        coEvery { repository.createDeck(name, cardIds) } returns expected

        val result = useCase(name, cardIds)

        result shouldBe expected
        coVerify(exactly = 1) { repository.createDeck(name, cardIds) }
    }
}