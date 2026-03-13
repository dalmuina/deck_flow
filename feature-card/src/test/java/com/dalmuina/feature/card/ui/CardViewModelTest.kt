package com.dalmuina.feature.card.ui

import app.cash.turbine.test
import com.dalmuina.coretest.data.CardTestData
import com.dalmuina.coretest.data.DeckTestData
import com.dalmuina.coretest.helpers.awaitLoaded
import com.dalmuina.coretest.helpers.success
import com.dalmuina.coretest.rules.MainDispatcherRule
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardViewModelRobot {

    val getSelectedDeckUseCase = mockk<GetSelectedDeckUseCase>(relaxed = true)
    val getDeckByIdUseCase = mockk<GetDeckByIdUseCase>(relaxed = true)

    fun build() =
        CardViewModel(
            getSelectedDeckUseCase,
            getDeckByIdUseCase
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
class CardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: CardViewModelRobot
    private lateinit var viewModel: CardViewModel

    @Before
    fun setup() {
        robot = CardViewModelRobot()
    }

    @Test
    fun `when viewModel starts then emits loading`() = runTest {

        every { robot.getSelectedDeckUseCase() } returns flowOf(null)

        viewModel = robot.build()

        viewModel.uiState.test {

            val loading = awaitItem()

            loading.loading shouldBe true

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when no deck selected then emits empty state`() = runTest {

        every { robot.getSelectedDeckUseCase() } returns flowOf(null)

        viewModel = robot.build()

        viewModel.uiState.test {

            awaitItem()

            val state = awaitItem()

            state.cards shouldBe emptyList()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when deck selected then emits cards`() = runTest {

        val deck = DeckTestData.deck(
            id = 1,
            cards = listOf(
                CardTestData.card(1),
                CardTestData.card(2),
                CardTestData.card(3)
            )
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(1)

        every { robot.getDeckByIdUseCase(1) } returns flowOf(
            success(deck)
        )

        viewModel = robot.build()

        viewModel.uiState.test {

            awaitItem() // loading

            val state = awaitItem()

            state.name shouldBe deck.name
            state.cards.size shouldBe 3

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when completeTopCard then moves first card to end`() = runTest {

        val deck = DeckTestData.deck(
            id = 1,
            cards = listOf(
                CardTestData.card(1),
                CardTestData.card(2)
            )
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(1)

        every { robot.getDeckByIdUseCase(1) } returns flowOf(
            success(deck)
        )

        viewModel = robot.build()

        viewModel.uiState.test {

            awaitItem() // loading

            val initial = awaitItem()

            viewModel.process(CardIntent.CompleteTopCard)

            val updated = awaitItem()

            updated.cards.first().id shouldBe 2
            updated.cards.last().id shouldBe 1

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when completeTopCard with empty cards then state unchanged`() = runTest {

        val deck = DeckTestData.deck(
            id = 1,
            cards = emptyList()
        )

        every { robot.getSelectedDeckUseCase() } returns flowOf(1)

        every { robot.getDeckByIdUseCase(1) } returns flowOf(
            success(deck)
        )

        viewModel = robot.build()

        viewModel.uiState.test {

            awaitItem() // loading

            val initial = awaitItem()

            viewModel.process(CardIntent.CompleteTopCard)

            expectNoEvents()

            initial.cards shouldBe emptyList()

            cancelAndIgnoreRemainingEvents()
        }
    }
}