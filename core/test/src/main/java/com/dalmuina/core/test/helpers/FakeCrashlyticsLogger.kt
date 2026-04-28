package com.dalmuina.core.test.helpers

import com.dalmuina.core.data.helpers.CrashlyticsLogger

class FakeCrashlyticsLogger : CrashlyticsLogger {
    val loggedExceptions = mutableListOf<Throwable>()

    override fun logException(e: Throwable, context: Map<String, String>) {
        loggedExceptions.add(e)
    }
    override fun logError(message: String, context: Map<String, String>) {}
}