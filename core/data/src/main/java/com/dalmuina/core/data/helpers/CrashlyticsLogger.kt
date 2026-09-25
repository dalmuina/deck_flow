package com.dalmuina.core.data.helpers

interface CrashlyticsLogger {
    fun logException(
        e: Throwable,
        context: Map<String, String> = emptyMap(),
    )

    fun logError(
        message: String,
        context: Map<String, String> = emptyMap(),
    )
}
