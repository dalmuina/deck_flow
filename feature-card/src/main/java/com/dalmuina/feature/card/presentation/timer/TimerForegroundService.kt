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
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.ObserveTimerStateUseCase
import com.dalmuina.feature.card.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TimerForegroundService : Service() {

    private val observeTimerState: ObserveTimerStateUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var watchJob: Job? = null

    companion object {
        const val ACTION_START = "com.dalmuina.ACTION_TIMER_START"
        const val ACTION_STOP = "com.dalmuina.ACTION_TIMER_STOP"
        private const val EXTRA_END_TIME_MILLIS = "extra_end_time_millis"
        private const val EXTRA_CARD_NAME = "extra_card_name"
        private const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "timer_channel"

        fun startIntent(context: Context, endTimeMillis: Long, cardName: String): Intent =
            Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_END_TIME_MILLIS, endTimeMillis)
                putExtra(EXTRA_CARD_NAME, cardName)
            }

        fun stopIntent(context: Context): Intent =
            Intent(context, TimerForegroundService::class.java).apply { action = ACTION_STOP }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundWithTimer(intent)
            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun startForegroundWithTimer(intent: Intent) {
        val endTimeMillis = intent.getLongExtra(EXTRA_END_TIME_MILLIS, -1L)
        val cardName = intent.getStringExtra(EXTRA_CARD_NAME) ?: ""

        if (endTimeMillis == -1L) {
            stopSelf()
            return
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("--:--", cardName))

        watchJob?.cancel()
        watchJob = serviceScope.launch {

            launch {
                while (true) {
                    val remaining = (endTimeMillis - System.currentTimeMillis())
                        .coerceAtLeast(0L)

                    if (remaining == 0L) {
                        watchJob?.children?.forEach { it.cancel() }
                        break
                    }

                    updateTimerNotification(remaining, NOTIFICATION_ID) { buildNotification(it, cardName) }

                    val nextTick = remaining % 1000
                    delay(if (nextTick > 0L) nextTick else 1000L)
                }
            }

            launch {
                observeTimerState()
                    .catch {
                        watchJob?.children?.forEach { it.cancel() }
                    }
                    .collect { result ->
                        if (result is DFResult.Success && !result.data.isRunning) {
                            watchJob?.children?.forEach { it.cancel() }
                        }
                    }
            }
            watchJob?.children?.forEach { it.join() }
            stopSelf()
        }
    }

    private fun buildNotification(timeText: String, cardName: String): Notification {
        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setContentTitle(getString(R.string.timer_notification_title, cardName))
            .setContentText(getString(R.string.timer_notification_text, timeText))
            .setContentIntent(tapIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.timer_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { setShowBadge(false) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        watchJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
