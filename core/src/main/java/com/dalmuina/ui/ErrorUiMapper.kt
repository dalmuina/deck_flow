package com.dalmuina.ui

import com.dalmuina.core.R
import com.dalmuina.domain.model.DataBaseError

fun DataBaseError.toUiMessage(): Int {
    return when (this) {

        DataBaseError.ConstraintViolation ->
            R.string.error_constraint

        is DataBaseError.Unknown ->
            R.string.error_unknown
    }
}