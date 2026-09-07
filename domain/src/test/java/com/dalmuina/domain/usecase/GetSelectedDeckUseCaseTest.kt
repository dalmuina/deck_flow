package com.dalmuina.domain.usecase

import app.cash.turbine.test
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.model.DFResult
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

    private val repository: SelectedDeckDataSource = mockk()

    @Test
    fun `invoke should emit selected deck id when repository returns value`() =
        runTest {
            val expected = 3

            every { repository.selectedDeckId } returns flowOf(DFResult.Success(expected))

            val useCase = GetSelectedDeckUseCase(repository)

            useCase().test {
                awaitItem() shouldBe DFResult.Success(expected)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should emit values from repository in order`() =
        runTest {
            every { repository.selectedDeckId } returns
                flowOf(
                    DFResult.Success(1),
                    DFResult.Success(2),
                    DFResult.Success(3),
                )

            val useCase = GetSelectedDeckUseCase(repository)

            useCase().test {
                awaitItem() shouldBe DFResult.Success(1)
                awaitItem() shouldBe DFResult.Success(2)
                awaitItem() shouldBe DFResult.Success(3)
                awaitComplete()
            }
        }
}
