package com.dalmuina.domain.usecase

import app.cash.turbine.test
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.SelectedDeckRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSelectedDeckUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: SelectedDeckRepository = mockk()

    @Test
    fun `invoke should emit selected deck id when repository returns value`() = runTest {
        val expected = 3

        every { repository.selectedDeckId } returns flowOf(expected)

        val useCase = GetSelectedDeckUseCase(repository)

        useCase().test {
            awaitItem() shouldBe expected
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit values from repository in order`() = runTest {
        every { repository.selectedDeckId } returns flowOf(1, 2, 3)

        val useCase = GetSelectedDeckUseCase(repository)

        useCase().test {
            awaitItem() shouldBe 1
            awaitItem() shouldBe 2
            awaitItem() shouldBe 3
            awaitComplete()
        }
    }
}