package com.dalmuina.domain.model

sealed interface DFError

sealed interface DataError : DFError {
    sealed interface Network : DataError {
        data object BadRequest : Network

        data object RequestTimeout : Network

        data object Unauthorized : Network

        data object Forbidden : Network

        data object NotFound : Network

        data object Conflict : Network

        data object TooManyRequests : Network

        data object NoInternet : Network

        data object PayloadTooLarge : Network

        data object ServerError : Network

        data object ServiceUnavailable : Network

        data object Serialization : Network

        data class Unknown(
            val throwable: Throwable? = null,
        ) : Network
    }

    sealed interface Local : DataError {
        data object DiskFull : Local

        data object NotFound : Local

        data object ConstraintViolation : Local

        data class Unknown(
            val throwable: Throwable? = null,
        ) : Local
    }

    sealed interface Preferences : DataError {
        data object Storage : Preferences

        data class Unknown(
            val throwable: Throwable? = null,
        ) : Preferences
    }
}
