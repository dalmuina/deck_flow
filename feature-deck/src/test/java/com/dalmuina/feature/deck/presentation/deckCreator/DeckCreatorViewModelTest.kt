package com.dalmuina.feature.deck.presentation.deckCreator

import app.cash.turbine.test
import com.dalmuina.core.test.data.ErrorTestData
import com.dalmuina.core.test.helpers.awaitLoaded
import com.dalmuina.core.test.helpers.failure
import com.dalmuina.core.test.helpers.success
import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.test.data.CardDomainTestData
import com.dalmuina.domain.usecase.SetDeckCardsUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeckCreatorViewModelRobot {

    val getAllCardsUseCase = mockk<GetAllCardsUseCase>(relaxed = true)
    val createDeckUseCase = mockk<CreateDeckUseCase>(relaxed = true)
    val updateDeckNameUseCase = mockk<UpdateDeckNameUseCase>(relaxed = true)
    val setDeckCardsUseCase = mockk<SetDeckCardsUseCase>(relaxed = true)
    val getDeckByIdUseCase = mockk<GetDeckByIdUseCase>(relaxed = true)
    val deleteCardUseCase = mockk<DeleteCardUseCase>(relaxed = true)
    val uiEventDispatcher = mockk<UiEventDispatcher>(relaxed = true)

    fun build(mode: DeckCreatorMode) =
        DeckCreatorViewModel(
            mode = mode,
            getAllCardsUseCase = getAllCardsUseCase,
            createDeckUseCase = createDeckUseCase,
            updateDeckNameUseCase = updateDeckNameUseCase,
            getDeckByIdUseCase = getDeckByIdUseCase,
            deleteCardUseCase = deleteCardUseCase,
            setDeckCardsUseCase = setDeckCardsUseCase,
            uiEventDispatcher = uiEventDispatcher
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
class DeckCreatorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: DeckCreatorViewModelRobot
    private lateinit var viewModel: DeckCreatorViewModel

    @Before
    fun setup() {
        robot = DeckCreatorViewModelRobot()
    }

    @Test
    fun `when cards loaded then emits cards in uiState`() = runTest {

        val cards = CardDomainTestData.cards(1, 2, 3)

        every { robot.getAllCardsUseCase() } returns flowOf(
            success(cards)
        )

        viewModel = robot.build(DeckCreatorMode.Create)

        viewModel.uiState.test {

            val state = awaitLoaded()

            state.loading shouldBe false
            state.deckCard.size shouldBe 3

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when save deck then createDeckUseCase called`() = runTest {

        val cards = CardDomainTestData.cards(1, 2)

        every { robot.getAllCardsUseCase() } returns flowOf(
            success(cards)
        )

        coEvery {
            robot.createDeckUseCase(any(), any())
        } returns DFResult.Success(Unit)

        viewModel = robot.build(DeckCreatorMode.Create)

        viewModel.process(DeckCreatorIntent.SelectedCard(1))
        viewModel.process(DeckCreatorIntent.SaveDeck)

        advanceUntilIdle()

        coVerify {
            robot.createDeckUseCase(any(), listOf(1))
        }
    }

    @Test
    fun `when name changes in edit mode then updateDeckNameUseCase called`() = runTest {

        every { robot.getAllCardsUseCase() } returns flowOf(
            success(emptyList())
        )

        viewModel = robot.build(DeckCreatorMode.Edit(5))

        viewModel.process(DeckCreatorIntent.NameChanged("New name"))

        advanceTimeBy(1000)
        advanceUntilIdle()

        coVerify {
            robot.updateDeckNameUseCase(5, "New name")
        }
    }

    @Test
    fun `when selecting card in edit mode then addCardToDeckUseCase called`() = runTest {

        val cards = CardDomainTestData.cards(1)

        every { robot.getAllCardsUseCase() } returns flowOf(
            success(cards)
        )

        viewModel = robot.build(DeckCreatorMode.Edit(10))

        viewModel.process(DeckCreatorIntent.SelectedCard(1))

        advanceUntilIdle()

        coVerify {
            robot.setDeckCardsUseCase(10,listOf(1))
        }
    }

    @Test
    fun `when delete card fails then dispatch snackbar`() = runTest {

        coEvery { robot.deleteCardUseCase(1) } returns failure(ErrorTestData.unknown)

        viewModel = robot.build(DeckCreatorMode.Create)

        viewModel.process(DeckCreatorIntent.DeleteCard(1))

        advanceUntilIdle()

        coVerify {
            robot.uiEventDispatcher.dispatch(any())
        }
    }
}