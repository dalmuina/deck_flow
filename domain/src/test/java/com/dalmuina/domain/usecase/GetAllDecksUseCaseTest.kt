package com.dalmuina.domain.usecase

import app.cash.turbine.test
import com.dalmuina.core.test.data.DeckDomainTestData
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllDecksUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocalDeckRepository = mockk()

    @Test
    fun `invoke should emit decks when repository returns success`() = runTest {
        val decks = DeckDomainTestData.decks(1, 2, 3)
        val expected = DFResult.Success<List<DFDeckDomain>>(decks)

        every { repository.getAllDecksWithCards() } returns flowOf(expected)

        val useCase = GetAllDecksUseCase(repository)

        useCase().test {
            awaitItem() shouldBe expected
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit values from repository in order`() = runTest {
        val first = DFResult.Success(DeckDomainTestData.decks(1))
        val second = DFResult.Success(DeckDomainTestData.decks(1, 2))
        val third = DFResult.Error<DataBaseError>(DataBaseError.ConstraintViolation)

        every { repository.getAllDecksWithCards() } returns flowOf(first, second, third)

        val useCase = GetAllDecksUseCase(repository)

        useCase().test {
            awaitItem() shouldBe first
            awaitItem() shouldBe second
            awaitItem() shouldBe third
            awaitComplete()
        }
    }
}