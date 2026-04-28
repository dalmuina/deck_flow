package com.dalmuina.core.data.helpers


import androidx.datastore.core.IOException
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safePreferencesCall(
    logger: CrashlyticsLogger,
    crossinline call: suspend () -> T
): DFResult<T, DataError> {
    return try {
        DFResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        logger.logException(e, mapOf("source" to "datastore"))
        DFResult.Error(DataError.Preferences.Storage)
    } catch (e: Exception) {
        logger.logException(e, mapOf("source" to "datastore"))
        DFResult.Error(DataError.Preferences.Unknown(e))
    }
}
