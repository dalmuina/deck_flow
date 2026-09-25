package com.dalmuina.feature.card.presentation.timer

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import java.util.Locale

fun Long.toTimerText(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
    }
}

fun Service.updateTimerNotification(
    remainingMillis: Long,
    notificationId: Int,
    buildNotification: (String) -> Notification,
) {
    getSystemService(NotificationManager::class.java)
        .notify(notificationId, buildNotification(remainingMillis.toTimerText()))
}
