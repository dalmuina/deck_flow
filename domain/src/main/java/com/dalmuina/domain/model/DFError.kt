package com.dalmuina.domain.model

sealed interface DFError

sealed interface DataBaseError: DFError {
    data class Unknown(val throwable: Throwable) :  DataBaseError
}