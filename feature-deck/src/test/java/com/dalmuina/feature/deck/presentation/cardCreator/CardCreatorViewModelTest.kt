package com.dalmuina.feature.deck.presentation.cardCreator

import app.cash.turbine.test
import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.core.test.data.ErrorTestData
import com.dalmuina.core.test.helpers.failure
import com.dalmuina.core.test.helpers.success
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.FIXED_CARD_DURATION_MILLIS
import com.dalmuina.domain.model.MAX_CARD_DESCRIPTION_LENGTH
import com.dalmuina.domain.usecase.GetCardByIdUseCase
import com.dalmuina.domain.usecase.SaveCardUseCase
import com.dalmuina.domain.usecase.UpdateCardUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardCreatorViewModelRobot {
    val saveCardUseCase = mockk<SaveCardUseCase>(relaxed = true)
    val updateCardUseCase = mockk<UpdateCardUseCase>(relaxed = true)
    val getCardByIdUseCase = mockk<GetCardByIdUseCase>(relaxed = true)
    val uiEventDispatcher = mockk<UiEventDispatcher>(relaxed = true)

    fun build(mode: CardCreatorMode) =
        CardCreatorViewModel(
            mode = mode,
            saveCardUseCase = saveCardUseCase,
            updateCardUseCase = updateCardUseCase,
            getCardByIdUseCase = getCardByIdUseCase,
            uiEventDispatcher = uiEventDispatcher,
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
class CardCreatorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: CardCreatorViewModelRobot
    private lateinit var viewModel: CardCreatorViewModel

    @Before
    fun setup() {
        robot = CardCreatorViewModelRobot()
    }

    @Test
    fun `when name changes then uiState updates`() =
        runTest {
            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.NameChanged("Test card"))

            viewModel.uiState.test {
                val state = awaitItem()

                state.name shouldBe "Test card"

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when description changes then uiState updates`() =
        runTest {
            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.DescriptionChanged("Test description"))

            viewModel.uiState.test {
                val state = awaitItem()

                state.description shouldBe "Test description"

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when description exceeds max length then uiState is truncated`() =
        runTest {
            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.DescriptionChanged("a".repeat(MAX_CARD_DESCRIPTION_LENGTH + 50)))

            viewModel.uiState.test {
                val state = awaitItem()

                state.description.length shouldBe MAX_CARD_DESCRIPTION_LENGTH

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when edit mode loads card then uiState populated`() =
        runTest {
            val card = CardDomainTestData.card(5, description = "Existing notes")

            coEvery { robot.getCardByIdUseCase(5) } returns success(card)

            viewModel = robot.build(CardCreatorMode.Edit(5))

            viewModel.uiState.test {
                val loading = awaitItem()
                loading.loading shouldBe true

                val loaded = awaitItem()

                loaded.loading shouldBe false
                loaded.name shouldBe card.name
                loaded.description shouldBe "Existing notes"

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when save card in create mode then saveCardUseCase called`() =
        runTest {
            val slot = slot<CardDomain>()
            coEvery { robot.saveCardUseCase(capture(slot)) } returns success(7)

            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.NameChanged("Test"))
            viewModel.process(CardCreatorIntent.SaveActivity)

            advanceUntilIdle()

            slot.captured.durationMillis shouldBe FIXED_CARD_DURATION_MILLIS
            slot.captured.description shouldBe null
        }

    @Test
    fun `when save card with description then saveCardUseCase receives description`() =
        runTest {
            val slot = slot<CardDomain>()
            coEvery { robot.saveCardUseCase(capture(slot)) } returns success(7)

            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.NameChanged("Test"))
            viewModel.process(CardCreatorIntent.DescriptionChanged("Some notes"))
            viewModel.process(CardCreatorIntent.SaveActivity)

            advanceUntilIdle()

            slot.captured.description shouldBe "Some notes"
            slot.captured.durationMillis shouldBe FIXED_CARD_DURATION_MILLIS
        }

    @Test
    fun `when save succeeds then emits CloseScreen event`() =
        runTest {
            coEvery { robot.saveCardUseCase(any()) } returns success(7)

            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.events.test {
                viewModel.process(CardCreatorIntent.SaveActivity)

                advanceUntilIdle()

                awaitItem() shouldBe CardCreatorEvent.CloseScreen(7)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when save fails then dispatch snackbar`() =
        runTest {
            coEvery { robot.saveCardUseCase(any()) } returns failure(ErrorTestData.unknown)

            viewModel = robot.build(CardCreatorMode.Create)

            viewModel.process(CardCreatorIntent.SaveActivity)

            advanceUntilIdle()

            coVerify {
                robot.uiEventDispatcher.dispatch(any())
            }
        }
}
