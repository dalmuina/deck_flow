package com.dalmuina.feature.card.presentation.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.usecase.ObserveTimerStateUseCase
import com.dalmuina.feature.card.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TimerForegroundService : Service() {
    private val observeTimerState: ObserveTimerStateUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var watchJob: Job? = null
    private var tickJob: Job? = null

    companion object {
        const val ACTION_START = "com.dalmuina.ACTION_TIMER_START"
        const val ACTION_STOP = "com.dalmuina.ACTION_TIMER_STOP"
        private const val EXTRA_END_TIME_MILLIS = "extra_end_time_millis"
        private const val EXTRA_CARD_NAME = "extra_card_name"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "timer_channel"

        fun startIntent(
            context: Context,
            endTimeMillis: Long,
            cardName: String,
        ): Intent =
            Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_END_TIME_MILLIS, endTimeMillis)
                putExtra(EXTRA_CARD_NAME, cardName)
            }

        fun stopIntent(context: Context): Intent = Intent(context, TimerForegroundService::class.java).apply { action = ACTION_STOP }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundWithTimer(intent)
            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun startForegroundWithTimer(intent: Intent) {
        val initialEndTimeMillis = intent.getLongExtra(EXTRA_END_TIME_MILLIS, -1L)
        val cardName = intent.getStringExtra(EXTRA_CARD_NAME) ?: ""

        if (initialEndTimeMillis == -1L) {
            stopSelf()
            return
        }

        createNotificationChannel()
        val initialText = (initialEndTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L).toTimerText()
        startForeground(NOTIFICATION_ID, buildNotification(isRunning = true, timeText = initialText, cardName))

        watchJob?.cancel()
        watchJob =
            serviceScope.launch {
                observeTimerState()
                    .catch { }
                    .collect { result ->
                        if (result !is DFResult.Success) return@collect
                        onTimerStateChanged(result.data, cardName)
                    }
            }
    }

    private fun onTimerStateChanged(
        state: PersistedTimerState,
        cardName: String,
    ) {
        tickJob?.cancel()

        if (state.cardId == null) {
            // No active card left to time (e.g. completed via the notification) - nothing to show.
            tickJob = null
            stopSelf()
            return
        }

        val endTimeMillis = state.endTimeMillis
        tickJob =
            if (state.isRunning && endTimeMillis != null) {
                serviceScope.launch { tickWhileRunning(endTimeMillis, cardName) }
            } else {
                notify(buildNotification(isRunning = false, timeText = state.remainingMillis.toTimerText(), cardName))
                null
            }
    }

    private suspend fun tickWhileRunning(
        endTimeMillis: Long,
        cardName: String,
    ) {
        while (currentCoroutineContext().isActive) {
            val now = System.currentTimeMillis()
            val remaining = endTimeMillis - now

            val timeText =
                if (remaining > 0) {
                    remaining.toTimerText()
                } else {
                    "+${(-remaining).toTimerText()}"
                }

            notify(buildNotification(isRunning = true, timeText = timeText, cardName))

            val nextTick = if (remaining > 0) remaining % 1000 else 1000L
            delay(if (nextTick > 0L) nextTick else 1000L)
        }
    }

    private fun notify(notification: Notification) {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        isRunning: Boolean,
        timeText: String,
        cardName: String,
    ): Notification {
        val tapIntent =
            PendingIntent.getActivity(
                this,
                0,
                packageManager.getLaunchIntentForPackage(packageName),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val toggleAction =
            if (isRunning) {
                NotificationCompat.Action(
                    R.drawable.ic_notification_pause,
                    getString(R.string.timer_action_pause),
                    TimerNotificationActionReceiver.togglePendingIntent(this),
                )
            } else {
                NotificationCompat.Action(
                    R.drawable.ic_notification_play,
                    getString(R.string.timer_action_play),
                    TimerNotificationActionReceiver.togglePendingIntent(this),
                )
            }

        val completeAction =
            NotificationCompat.Action(
                R.drawable.ic_notification_check,
                getString(R.string.timer_action_complete),
                TimerNotificationActionReceiver.completePendingIntent(this),
            )

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setContentTitle(getString(R.string.timer_notification_title, cardName))
            .setContentText(getString(R.string.timer_notification_text, timeText))
            .setContentIntent(tapIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(toggleAction)
            .addAction(completeAction)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.timer_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { setShowBadge(false) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        tickJob?.cancel()
        watchJob?.cancel()
        serviceScope.cancel()
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        super.onDestroy()
    }
}
