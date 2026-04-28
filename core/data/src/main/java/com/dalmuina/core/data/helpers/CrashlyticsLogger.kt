package com.dalmuina.core.data.helpers

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsLogger {
    
    fun logException(e: Throwable, context: Map<String, String> = emptyMap()) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        context.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.recordException(e)
    }

    fun logError(message: String, context: Map<String, String> = emptyMap()) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        context.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.log(message)
    }
}