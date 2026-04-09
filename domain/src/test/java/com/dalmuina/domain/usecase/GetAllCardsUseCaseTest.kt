package com.dalmuina.domain.usecase

import app.cash.turbine.test
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllCardsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CardLocalDataSource = mockk()

    @Test
    fun `invoke should emit cards when repository returns success`() = runTest {
        val cards = CardDomainTestData.cards(1, 2, 3)
        val expected = DFResult.Success(cards)

        every { repository.getAllCards() } returns flowOf(expected)

        val useCase = GetAllCardsUseCase(repository)

        useCase().test {
            awaitItem() shouldBe expected
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit values from repository in order`() = runTest {
        val first = DFResult.Success(CardDomainTestData.cards(1))
        val second = DFResult.Success(CardDomainTestData.cards(1, 2))
        val third = DFResult.Error<DataError.Local>(DataError.Local.ConstraintViolation)

        every { repository.getAllCards() } returns flowOf(first, second, third)

        val useCase = GetAllCardsUseCase(repository)

        useCase().test {
            awaitItem() shouldBe first
            awaitItem() shouldBe second
            awaitItem() shouldBe third
            awaitComplete()
        }
    }
}
