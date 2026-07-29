package com.dalmuina.feature.card.presentation.timer

import com.dalmuina.core.test.rules.MainDispatcherRule
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.usecase.ObserveTimerStateUseCase
import com.dalmuina.domain.usecase.SaveTimerStateUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerViewModelRobot {

    val observeTimerStateUseCase = mockk<ObserveTimerStateUseCase>()
    val saveTimerStateUseCase = mockk<SaveTimerStateUseCase>(relaxed = true)

    fun build() = TimerViewModel(observeTimerStateUseCase, saveTimerStateUseCase)
}

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var robot: TimerViewModelRobot
    private lateinit var viewModel: TimerViewModel

    @Before
    fun setup() {
        robot = TimerViewModelRobot()
    }

    @Test
    fun `when reset then persists cardId`() = runTest {
        every { robot.observeTimerStateUseCase() } returns flowOf(
            DFResult.Success(
                PersistedTimerState()
            )
        )

        viewModel = robot.build()
        runCurrent()

        viewModel.process(TimerIntent.Reset(durationMillis = 60_000L, cardId = 7))
        runCurrent()

        val slot = slot<PersistedTimerState>()
        coVerify { robot.saveTimerStateUseCase(capture(slot)) }

        slot.captured.cardId shouldBe 7
        slot.captured.totalMillis shouldBe 60_000L
        slot.captured.isRunning shouldBe false
    }

    @Test
    fun `when restoring a running persisted state then timer state reflects it running`() =
        runTest {
            val now = System.currentTimeMillis()
            val persisted = PersistedTimerState(
                totalMillis = 10_000L,
                isRunning = true,
                endTimeMillis = now + 5_000L,
                cardId = 3,
            )
            every { robot.observeTimerStateUseCase() } returns flowOf(DFResult.Success(persisted))

            viewModel = robot.build()
            runCurrent()

            viewModel.timerState.value.isRunning shouldBe true
            viewModel.timerState.value.totalMillis shouldBe 10_000L
            viewModel.isLoaded.value shouldBe true

            // Stop the ticker to avoid hanging the test
            viewModel.process(TimerIntent.Pause)
            runCurrent()
        }

    @Test
    fun `when restoring a paused persisted state then timer state reflects it paused`() = runTest {
        val persisted = PersistedTimerState(
            totalMillis = 10_000L,
            remainingMillis = 4_000L,
            elapsedMillis = 6_000L,
            isRunning = false,
            cardId = 9,
        )
        every { robot.observeTimerStateUseCase() } returns flowOf(DFResult.Success(persisted))

        viewModel = robot.build()
        runCurrent()

        viewModel.timerState.value.isRunning shouldBe false
        viewModel.timerState.value.remainingMillis shouldBe 4_000L
    }

    @Test
    fun `when pausing after restoring persisted cardId then it saves the same cardId`() = runTest {
        val now = System.currentTimeMillis()
        val persisted = PersistedTimerState(
            totalMillis = 10_000L,
            isRunning = true,
            endTimeMillis = now + 5_000L,
            cardId = 11,
        )
        every { robot.observeTimerStateUseCase() } returns flowOf(DFResult.Success(persisted))

        viewModel = robot.build()
        runCurrent()

        viewModel.process(TimerIntent.Pause)
        runCurrent()

        val slot = slot<PersistedTimerState>()
        coVerify { robot.saveTimerStateUseCase(capture(slot)) }

        slot.captured.cardId shouldBe 11
    }

    @Test
    fun `when Sync then re-pulls persisted state without resetting isLoaded`() = runTest {
        every { robot.observeTimerStateUseCase() } returns flowOf(
            DFResult.Success(
                PersistedTimerState()
            )
        )

        viewModel = robot.build()
        runCurrent()

        viewModel.isLoaded.value shouldBe true

        every { robot.observeTimerStateUseCase() } returns flowOf(
            DFResult.Success(
                PersistedTimerState(totalMillis = 20_000L, remainingMillis = 20_000L, cardId = 4)
            )
        )

        viewModel.process(TimerIntent.Sync)
        runCurrent()

        viewModel.timerState.value.totalMillis shouldBe 20_000L
        viewModel.isLoaded.value shouldBe true
    }
}
