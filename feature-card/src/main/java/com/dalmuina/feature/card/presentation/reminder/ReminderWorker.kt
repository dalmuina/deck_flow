package com.dalmuina.feature.card.presentation.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.feature.card.R
import kotlinx.coroutines.flow.first

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
    private val getSelectedDeck: GetSelectedDeckUseCase,
    private val getDeckById: GetDeckByIdUseCase,
) : CoroutineWorker(context, params) {

    companion object {
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "reminder_channel"
    }

    override suspend fun doWork(): Result {
        val deckId = (getSelectedDeck().first() as? DFResult.Success)?.data ?: return Result.success()
        val deck = (getDeckById(deckId).first() as? DFResult.Success)?.data ?: return Result.success()
        val pendingCard = deck.cards.firstOrNull { it.completedAt == null } ?: return Result.success()

        showNotification(deckName = deck.name, cardName = pendingCard.name)
        return Result.success()
    }

    private fun showNotification(deckName: String, cardName: String) {
        createNotificationChannel()
        val notification = buildNotification(deckName, cardName)
        applicationContext.getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(deckName: String, cardName: String): Notification {
        val tapIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName),
            PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setContentTitle(applicationContext.getString(R.string.reminder_notification_title, deckName))
            .setContentText(applicationContext.getString(R.string.reminder_notification_text, cardName))
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.reminder_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
