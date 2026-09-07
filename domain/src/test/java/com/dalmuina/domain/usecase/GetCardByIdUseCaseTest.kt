package com.dalmuina.domain.usecase

import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.CardLocalDataSource
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
class GetCardByIdUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CardLocalDataSource = mockk()

    @Test
    fun `invoke should return card when repository returns success`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                GetCardByIdUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val cardId = 1
            val card = CardDomainTestData.card(id = cardId)
            val expected = DFResult.Success(card)

            coEvery { repository.getCardById(cardId) } returns expected

            val result = useCase(cardId)

            result shouldBe expected
            coVerify(exactly = 1) { repository.getCardById(cardId) }
        }

    @Test
    fun `invoke should return error when repository fails getting card`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val useCase =
                GetCardByIdUseCase(
                    repository = repository,
                    dispatcher = dispatcher,
                )

            val cardId = 1
            val expected = DFResult.Error(DataError.Local.ConstraintViolation)

            coEvery { repository.getCardById(cardId) } returns expected

            val result = useCase(cardId)

            result shouldBe expected
            coVerify(exactly = 1) { repository.getCardById(cardId) }
        }
}
