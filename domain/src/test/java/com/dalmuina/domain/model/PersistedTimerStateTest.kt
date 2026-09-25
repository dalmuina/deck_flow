package com.dalmuina.domain.model

import io.kotest.matchers.shouldBe
import org.junit.Test

class PersistedTimerStateTest {
    @Test
    fun `when pausedAt while running then freezes remaining and elapsed`() {
        val now = 10_000L
        val state =
            PersistedTimerState(
                totalMillis = 5_000L,
                endTimeMillis = now + 2_000L,
                isRunning = true,
                cardId = 1,
            )

        val paused = state.pausedAt(now)

        paused.isRunning shouldBe false
        paused.endTimeMillis shouldBe null
        paused.elapsedMillis shouldBe 3_000L
        paused.remainingMillis shouldBe 2_000L
        paused.cardId shouldBe 1
    }

    @Test
    fun `when resumedAt while paused then recomputes endTimeMillis from elapsed`() {
        val now = 10_000L
        val state =
            PersistedTimerState(
                totalMillis = 5_000L,
                elapsedMillis = 3_000L,
                remainingMillis = 2_000L,
                isRunning = false,
                cardId = 2,
            )

        val resumed = state.resumedAt(now)

        resumed.isRunning shouldBe true
        resumed.endTimeMillis shouldBe now - 3_000L + 5_000L
        resumed.cardId shouldBe 2
    }

    @Test
    fun `when resumedAt with no totalMillis then does nothing`() {
        val state = PersistedTimerState(isRunning = false)

        val resumed = state.resumedAt(10_000L)

        resumed shouldBe state
    }

    @Test
    fun `when resumedAt while already running then does nothing`() {
        val state = PersistedTimerState(totalMillis = 5_000L, isRunning = true, endTimeMillis = 12_000L)

        val resumed = state.resumedAt(10_000L)

        resumed shouldBe state
    }
}
