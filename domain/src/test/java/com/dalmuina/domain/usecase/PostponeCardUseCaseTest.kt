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
class PostponeCardUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocalCardRepository = mockk()

    @Test
    fun `invoke should return success when repository postpones card successfully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = PostponeCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val cardId = 1
        val expected = DFResult.Success(1)

        coEvery { repository.postponeCard(cardId) } returns expected

        val result = useCase(cardId)

        result shouldBe expected
        coVerify(exactly = 1) { repository.postponeCard(cardId) }
    }

    @Test
    fun `invoke should return error when repository fails postponing card`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = PostponeCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val cardId = 1
        val expected = DFResult.Error(DataBaseError.ConstraintViolation)

        coEvery { repository.postponeCard(cardId) } returns expected

        val result = useCase(cardId)

        result shouldBe expected
        coVerify(exactly = 1) { repository.postponeCard(cardId) }
    }
}