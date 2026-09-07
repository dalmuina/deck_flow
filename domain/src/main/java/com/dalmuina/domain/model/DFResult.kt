package com.dalmuina.domain.model

sealed interface DFResult<out D, out E : DFError> {
    data class Success<out D>(
        val data: D,
    ) : DFResult<D, Nothing>

    data class Error<out E : DFError>(
        val error: E,
    ) : DFResult<Nothing, E>
}

typealias EmptyResult<E> = DFResult<Unit, E>

inline fun <T, E : DFError, R> DFResult<T, E>.map(map: (T) -> R): DFResult<R, E> =
    when (this) {
        is DFResult.Error -> DFResult.Error(error)
        is DFResult.Success -> DFResult.Success(map(this.data))
    }

inline fun <T, E : DFError> DFResult<T, E>.onSuccess(action: (T) -> Unit): DFResult<T, E> =
    when (this) {
        is DFResult.Error -> this
        is DFResult.Success -> {
            action(data)
            this
        }
    }

inline fun <T, E : DFError> DFResult<T, E>.onFailure(action: (E) -> Unit): DFResult<T, E> =
    when (this) {
        is DFResult.Error -> {
            action(error)
            this
        }
        is DFResult.Success -> this
    }

fun <T, E : DFError> DFResult<T, E>.asEmptyResult(): EmptyResult<E> = map { }
