package com.dalmuina.feature.card.presentation.timer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.model.pausedAt
import com.dalmuina.domain.model.resumedAt
import com.dalmuina.domain.usecase.CompleteCardUseCase
import com.dalmuina.domain.usecase.ObserveTimerStateUseCase
import com.dalmuina.domain.usecase.SaveTimerStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TimerNotificationActionReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val observeTimerStateUseCase: ObserveTimerStateUseCase by inject()
    private val saveTimerStateUseCase: SaveTimerStateUseCase by inject()
    private val completeCardUseCase: CompleteCardUseCase by inject()

    companion object {
        const val ACTION_TOGGLE_PLAY_PAUSE = "com.dalmuina.ACTION_TIMER_TOGGLE"
        const val ACTION_COMPLETE = "com.dalmuina.ACTION_TIMER_COMPLETE"
        private const val REQUEST_CODE_TOGGLE = 1
        private const val REQUEST_CODE_COMPLETE = 2

        fun togglePendingIntent(context: Context): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_TOGGLE,
                Intent(context, TimerNotificationActionReceiver::class.java).setAction(ACTION_TOGGLE_PLAY_PAUSE),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        fun completePendingIntent(context: Context): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_COMPLETE,
                Intent(context, TimerNotificationActionReceiver::class.java).setAction(ACTION_COMPLETE),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_TOGGLE_PLAY_PAUSE -> handleToggle()
                    ACTION_COMPLETE -> handleComplete(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleToggle() {
        val persisted = currentPersistedState() ?: return
        val now = System.currentTimeMillis()
        val updated =
            if (persisted.isRunning) {
                persisted.pausedAt(now)
            } else {
                if (persisted.totalMillis == 0L) return
                persisted.resumedAt(now)
            }
        saveTimerStateUseCase(updated)
    }

    private suspend fun handleComplete(context: Context) {
        val persisted = currentPersistedState() ?: return
        val cardId = persisted.cardId ?: return
        val spentMillis = if (persisted.elapsedMillis > 0L) persisted.elapsedMillis else persisted.totalMillis

        completeCardUseCase(cardId, spentMillis)
        saveTimerStateUseCase(PersistedTimerState())

        NotificationManagerCompat.from(context).cancel(TimerForegroundService.NOTIFICATION_ID)
        context.stopService(Intent(context, TimerForegroundService::class.java))
    }

    private suspend fun currentPersistedState(): PersistedTimerState? = (observeTimerStateUseCase().first() as? DFResult.Success)?.data
}
