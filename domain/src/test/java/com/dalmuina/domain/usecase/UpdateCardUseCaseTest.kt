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
class UpdateCardUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DeckLocalDataSource = mockk()

    @Test
    fun `invoke should return success when repository updates deck name successfully`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                UpdateDeckNameUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val deckId = 1
            val name = "New name"
            val expected = DFResult.Success(Unit)

            coEvery { repository.updateDeckName(deckId, name) } returns expected

            val result = useCase(deckId, name)

            result shouldBe expected
            coVerify(exactly = 1) { repository.updateDeckName(deckId, name) }
        }

    @Test
    fun `invoke should return error when repository fails updating deck name`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                UpdateDeckNameUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val deckId = 1
            val name = "New name"
            val expected = DFResult.Error(DataError.Local.ConstraintViolation)

            coEvery { repository.updateDeckName(deckId, name) } returns expected

            val result = useCase(deckId, name)

            result shouldBe expected
            coVerify(exactly = 1) { repository.updateDeckName(deckId, name) }
        }
}
