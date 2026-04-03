package com.dalmuina.domain.usecase

import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.LocalCardRepository
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
class CompleteCardUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocalCardRepository = mockk()

    @Test
    fun `invoke should return success when repository completes card successfully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = CompleteCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val cardId = 1
        val spentMillis = 15000L
        val expected = DFResult.Success(1)

        coEvery { repository.completeCard(cardId,spentMillis) } returns expected

        val result = useCase(cardId,spentMillis)

        result shouldBe expected
        coVerify(exactly = 1) { repository.completeCard(cardId,spentMillis) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = CompleteCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val cardId = 1
        val spentMillis = 15000L
        val expected = DFResult.Error(DataBaseError.ConstraintViolation)

        coEvery { repository.completeCard(cardId,spentMillis) } returns expected

        val result = useCase(cardId,spentMillis)

        result shouldBe expected
        coVerify(exactly = 1) { repository.completeCard(cardId,spentMillis) }
    }
}
