package com.dalmuina.data.repository


import android.database.sqlite.SQLiteConstraintException
import androidx.datastore.core.IOException
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safePreferencesCall(
    crossinline call: suspend () -> T
): DFResult<T, DataError.Preferences> {
    return try {
        DFResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        DFResult.Error(DataError.Preferences.Storage)
    } catch (e: Exception) {
        DFResult.Error(DataError.Preferences.Unknown(e))
    }
}
