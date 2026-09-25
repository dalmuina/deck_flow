package com.dalmuina.feature.card.presentation.reminder

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderSchedulerTest {
    @Test
    fun `millisUntilNextOccurrenceOf returns delay to today when target hour is still upcoming`() {
        val now = calendarAt(hour = 8, minute = 0)

        val result = ReminderScheduler.millisUntilNextOccurrenceOf(hour = 10, now = now)

        result shouldBe TimeUnit.HOURS.toMillis(2)
    }

    @Test
    fun `millisUntilNextOccurrenceOf rolls to tomorrow when target hour has already passed today`() {
        val now = calendarAt(hour = 21, minute = 0)

        val result = ReminderScheduler.millisUntilNextOccurrenceOf(hour = 10, now = now)

        result shouldBe TimeUnit.HOURS.toMillis(13)
    }

    @Test
    fun `millisUntilNextOccurrenceOf rolls to tomorrow when now is exactly at the target hour`() {
        val now = calendarAt(hour = 10, minute = 0)

        val result = ReminderScheduler.millisUntilNextOccurrenceOf(hour = 10, now = now)

        result shouldBe TimeUnit.DAYS.toMillis(1)
    }

    private fun calendarAt(
        hour: Int,
        minute: Int,
    ): Calendar =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
}
