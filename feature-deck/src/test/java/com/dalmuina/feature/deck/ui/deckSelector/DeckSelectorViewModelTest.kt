package com.dalmuina.feature.deck.ui.deckSelector

import app.cash.turbine.test
import com.dalmuina.coretest.data.DeckTestData
import com.dalmuina.coretest.data.ErrorTestData
import com.dalmuina.coretest.helpers.awaitLoaded
import com.dalmuina.coretest.helpers.failure
import com.dalmuina.coretest.helpers.success
import com.dalmuina.coretest.rules.MainDispatcherRule
import com.dalmuina.core.ui.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.DeleteDeckUseCase
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.SetSelectedDeckUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeckSelectorViewModelRobot {

    val getAllDecksUseCase = mockk<GetAllDecksUseCase>(relaxed = true)
    val deleteDeckUseCase = mockk<DeleteDeckUseCase>(relaxed = true)
    val getSelectedDeckUseCase = mockk<GetSelectedDeckUseCase>(relaxed = true)
    val setSelectedDeckUseCase = mockk<SetSelectedDeckUseCase>(relaxed = true)
    val uiEventDispatcher = mockk<UiEventDispatcher>(relaxed = true)

    fun build() = DeckSelectorViewModel(
        getAllDecksUseCase,
        deleteDeckUseCase,
        getSelectedDeckUseCase,
        setSelectedDeckUseCase,
        uiEventDispatcher
    )

}

@OptIn(ExperimentalCoroutinesApi::class)
class DeckSelectorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: DeckSelectorViewModelRobot
    private lateinit var viewModel: DeckSelectorViewModel

    @Before
    fun setup() {
        robot = DeckSelectorViewModelRobot()
    }

    @Test
    fun `when decks loaded then emits decks with selected`() = runTest {

        val decks = DeckTestData.decks(1,2,3)

        every { robot.getAllDecksUseCase() } returns flowOf(
            success(decks)
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(2)

        viewModel = robot.build()

        viewModel.uiState.test {

            val state = awaitLoaded()

            state.loading shouldBe false
            state.deckList.size shouldBe 3
            state.deckList.first { it.id == 2 }.isSelected shouldBe true

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when SelectDeck intent then calls setSelectedDeckUseCase`() = runTest {
        viewModel = robot.build()
        viewModel.process(DeckSelectorIntent.SelectDeck(2))

        advanceUntilIdle()

        coVerify(exactly = 1) {
            robot.setSelectedDeckUseCase(2)
        }
    }

    @Test
    fun `when deleting selected deck selects next deck`() = runTest {

        val decks = DeckTestData.decks(1,2,3)

        every { robot.getAllDecksUseCase() } returns flowOf(
            success(decks)
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(2)

        coEvery { robot.deleteDeckUseCase(2) } returns DFResult.Success(3)

        viewModel = robot.build()

        viewModel.uiState.test {

            awaitLoaded()

            viewModel.process(DeckSelectorIntent.DeleteDeck(2))

            advanceUntilIdle()

            coVerify { robot.deleteDeckUseCase(2) }
            coVerify { robot.setSelectedDeckUseCase(3) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when delete fails then dispatch snackbar`() = runTest {

        coEvery { robot.deleteDeckUseCase(1) } returns failure(ErrorTestData.unknown)

        viewModel = robot.build()
        viewModel.process(DeckSelectorIntent.DeleteDeck(1))

        advanceUntilIdle()

        coVerify {
            robot.uiEventDispatcher.dispatch(any())
        }
    }

    @Test
    fun `when no selected deck then first deck becomes selected`() = runTest {

        val decks = DeckTestData.decks(1,2,3)

        every { robot.getAllDecksUseCase() } returns flowOf(
            success(decks)
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(null)

        viewModel = robot.build()
        viewModel.uiState.test {

            val state = awaitLoaded()

            state.deckList.first { it.id == 1 }.isSelected shouldBe true

            cancelAndIgnoreRemainingEvents()
        }
    }
}
