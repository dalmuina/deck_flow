package com.dalmuina.feature.card.presentation.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private val REMINDER_HOURS = listOf(10, 20)

    fun scheduleDailyReminders(context: Context) {
        REMINDER_HOURS.forEach { hour ->
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(millisUntilNextOccurrenceOf(hour), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "reminder_$hour",
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }

    internal fun millisUntilNextOccurrenceOf(hour: Int, now: Calendar = Calendar.getInstance()): Long {
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
