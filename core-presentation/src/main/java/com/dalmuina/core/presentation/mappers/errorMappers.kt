package com.dalmuina.core.presentation.mappers


import com.dalmuina.core_presentation.R
import com.dalmuina.domain.model.DataError


fun DataError.toUiText(): Int {
    return when (this) {
        DataError.Local.ConstraintViolation -> R.string.error_constraint
        DataError.Local.DiskFull -> R.string.error_disk_full
        DataError.Local.NotFound -> R.string.error_not_found
        is DataError.Local.Unknown -> R.string.error_unknown

        DataError.Preferences.Storage -> R.string.error_storage
        is DataError.Preferences.Unknown -> R.string.error_unknown

        DataError.Network.BadRequest -> R.string.error_bad_request
        DataError.Network.RequestTimeout -> R.string.error_request_timeout
        DataError.Network.Unauthorized -> R.string.error_unauthorized
        DataError.Network.Forbidden -> R.string.error_forbidden
        DataError.Network.NotFound -> R.string.error_not_found
        DataError.Network.Conflict -> R.string.error_conflict
        DataError.Network.TooManyRequests -> R.string.error_too_many_requests
        DataError.Network.NoInternet -> R.string.error_no_internet
        DataError.Network.PayloadTooLarge -> R.string.error_payload_too_large
        DataError.Network.ServerError -> R.string.error_server_error
        DataError.Network.ServiceUnavailable -> R.string.error_service_unavailable
        DataError.Network.Serialization -> R.string.error_serialization
        is DataError.Network.Unknown -> R.string.error_unknown
    }
}