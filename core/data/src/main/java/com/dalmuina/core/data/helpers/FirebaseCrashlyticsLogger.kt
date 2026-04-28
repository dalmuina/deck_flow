package com.dalmuina.core.data.helpers

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashlyticsLogger: CrashlyticsLogger {
    
    override fun logException(e: Throwable, context: Map<String, String>) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        context.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.recordException(e)
    }

    override fun logError(message: String, context: Map<String, String>) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        context.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.log(message)
    }
}