package com.dalmuina.domain.model

sealed interface DFError

sealed interface DataBaseError: DFError {

    data object ConstraintViolation: DataBaseError
    data class Unknown(val throwable: Throwable) :  DataBaseError
}

sealed interface PreferencesError: DFError {
    data class Unknown(val throwable: Throwable) : PreferencesError
}