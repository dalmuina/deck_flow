package com.dalmuina.core.data.helpers

import android.database.sqlite.SQLiteConstraintException
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <reified T> safeDbCall(
    logger: CrashlyticsLogger,
    crossinline call: suspend () -> T
): DFResult<T, DataError> {
    return try {
        DFResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SQLiteConstraintException) {
        logger.logException(e, mapOf("source" to "db", "type" to "constraint"))
        DFResult.Error(DataError.Local.ConstraintViolation)
    } catch (e: Exception) {
        logger.logException(e, mapOf("source" to "db"))
        DFResult.Error(DataError.Local.Unknown(e))
    }
}
