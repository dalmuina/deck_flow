package com.dalmuina.core.data.helpers

import com.dalmuina.domain.model.DataError

fun mapHttpCodeToNetworkError(
    code: Int,
    throwable: Throwable? = null
): DataError {
    return when (code) {
        400 -> DataError.Network.BadRequest
        401 -> DataError.Network.Unauthorized
        403 -> DataError.Network.Forbidden
        404 -> DataError.Network.NotFound
        408 -> DataError.Network.RequestTimeout
        409 -> DataError.Network.Conflict
        413 -> DataError.Network.PayloadTooLarge
        429 -> DataError.Network.TooManyRequests
        500 -> DataError.Network.ServerError
        503 -> DataError.Network.ServiceUnavailable
        else -> DataError.Network.Unknown(throwable)
    }
}
