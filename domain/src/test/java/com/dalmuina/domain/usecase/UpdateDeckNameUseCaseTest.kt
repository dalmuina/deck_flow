package com.dalmuina.domain.usecase

import com.dalmuina.coretest.data.CardDomainTestData
import com.dalmuina.coretest.rules.MainDispatcherRule
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
class UpdateDeckNameUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocalCardRepository = mockk()

    @Test
    fun `invoke should return success when repository updates card successfully`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = UpdateCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val card = CardDomainTestData.card(id = 1)
        val expected = DFResult.Success(1)

        coEvery { repository.updateCard(card) } returns expected

        val result = useCase(card)

        result shouldBe expected
        coVerify(exactly = 1) { repository.updateCard(card) }
    }

    @Test
    fun `invoke should return error when repository fails updating card`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = UpdateCardUseCase(
            repository = repository,
            dispatcher = dispatcher
        )

        val card = CardDomainTestData.card(id = 1)
        val expected = DFResult.Error(DataBaseError.ConstraintViolation)

        coEvery { repository.updateCard(card) } returns expected

        val result = useCase(card)

        result shouldBe expected
        coVerify(exactly = 1) { repository.updateCard(card) }
    }
}