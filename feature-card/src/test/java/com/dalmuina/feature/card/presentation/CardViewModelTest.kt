package com.dalmuina.feature.card.presentation

import app.cash.turbine.test
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.data.DeckDomainTestData
import com.dalmuina.core.test.helpers.awaitLoaded
import com.dalmuina.core.test.helpers.success
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.CompleteCardUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.PostponeCardUseCase
import com.dalmuina.feature.card.presentation.session.CardIntent
import com.dalmuina.feature.card.presentation.session.CardViewModel
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

    val completeCardUseCase = mockk<CompleteCardUseCase>(relaxed = true)

    val postponeCardUseCase = mockk<PostponeCardUseCase>(relaxed = true)

    fun build() =
        CardViewModel(
            getSelectedDeckUseCase,
            getDeckByIdUseCase,
            completeCardUseCase,
            postponeCardUseCase,
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
    fun `when viewModel starts then emits loading`() =
        runTest {
            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(null))

            viewModel = robot.build()

            viewModel.uiState.test {
                val loading = awaitItem()

                loading.loading shouldBe true

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when no deck selected then emits empty state`() =
        runTest {
            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(null))

            viewModel = robot.build()

            viewModel.uiState.test {
                awaitItem()

                val state = awaitItem()

                state.cards shouldBe emptyList()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when deck selected then emits cards`() =
        runTest {
            val deck =
                DeckDomainTestData.deck(
                    id = 1,
                    cards =
                        listOf(
                            CardDomainTestData.card(1),
                            CardDomainTestData.card(2),
                            CardDomainTestData.card(3),
                        ),
                )

            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(1))

            every { robot.getDeckByIdUseCase(1) } returns
                flowOf(
                    success(deck),
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
    fun `when complete top card via dialog then first card is removed`() =
        runTest {
            val deck =
                DeckDomainTestData.deck(
                    id = 1,
                    cards =
                        listOf(
                            CardDomainTestData.card(1),
                            CardDomainTestData.card(2),
                        ),
                )

            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(1))

            every { robot.getDeckByIdUseCase(1) } returns
                flowOf(
                    success(deck),
                )

            viewModel = robot.build()

            viewModel.uiState.test {
                awaitLoaded()

                viewModel.process(CardIntent.RequestCompleteCard(15000L))
                awaitItem() // dialog opens

                viewModel.process(CardIntent.ConfirmCompletion)
                val updated = awaitItem()

                updated.cards.first().id shouldBe 2
                updated.cards.last().id shouldBe 2

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when dismiss completion then completionPending is cleared`() =
        runTest {
            val deck =
                DeckDomainTestData.deck(
                    id = 1,
                    cards = listOf(CardDomainTestData.card(1)),
                )

            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(1))
            every { robot.getDeckByIdUseCase(1) } returns flowOf(success(deck))

            viewModel = robot.build()

            viewModel.uiState.test {
                awaitLoaded()

                viewModel.process(CardIntent.RequestCompleteCard(60000L))
                val withDialog = awaitItem()
                withDialog.completionPending shouldBe withDialog.completionPending

                viewModel.process(CardIntent.DismissCompletion)
                val dismissed = awaitItem()

                dismissed.completionPending shouldBe null
                dismissed.cards.size shouldBe 1

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when completeTopCard with empty cards then state unchanged`() =
        runTest {
            val deck =
                DeckDomainTestData.deck(
                    id = 1,
                    cards = emptyList(),
                )

            every { robot.getSelectedDeckUseCase() } returns flowOf(DFResult.Success(1))

            every { robot.getDeckByIdUseCase(1) } returns
                flowOf(
                    success(deck),
                )

            viewModel = robot.build()

            viewModel.uiState.test {
                awaitItem() // loading

                val initial = awaitItem()

                viewModel.process(CardIntent.RequestCompleteCard(15000L))

                expectNoEvents()

                initial.cards shouldBe emptyList()

                cancelAndIgnoreRemainingEvents()
            }
        }
}
