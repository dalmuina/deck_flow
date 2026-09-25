package com.dalmuina.domain.usecase

import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.SelectedDeckDataSource
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
class SetSelectedDeckUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: SelectedDeckDataSource = mockk()

    @Test
    fun `invoke should return success when repository sets selected deck successfully`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                SetSelectedDeckUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val deckId = 3
            val expected = DFResult.Success(Unit)

            coEvery { repository.setSelectedDeck(deckId) } returns expected

            val result = useCase(deckId)

            result shouldBe expected
            coVerify(exactly = 1) { repository.setSelectedDeck(deckId) }
        }

    @Test
    fun `invoke should return error when repository fails setting selected deck`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                SetSelectedDeckUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val deckId = 3
            val throwable = Throwable("write failed")
            val expected = DFResult.Error(DataError.Preferences.Unknown(throwable))

            coEvery { repository.setSelectedDeck(deckId) } returns expected

            val result = useCase(deckId)

            result shouldBe expected
            coVerify(exactly = 1) { repository.setSelectedDeck(deckId) }
        }
}
