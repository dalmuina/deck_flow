package com.dalmuina.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safeDbCall(
    crossinline call: suspend () -> T
): DFResult<T, DataBaseError> {
    return try {
        DFResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SQLiteConstraintException) {
        DFResult.Error(DataBaseError.ConstraintViolation)
    } catch (e: Exception) {
        DFResult.Error(DataBaseError.Unknown(e))
    }
}