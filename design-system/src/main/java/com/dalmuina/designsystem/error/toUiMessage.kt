package com.dalmuina.designsystem.error

import com.dalmuina.designsystem.R
import com.dalmuina.domain.model.DataBaseError


fun DataBaseError.toUiMessage(): Int {
    return when (this) {

        DataBaseError.ConstraintViolation ->
            R.string.error_constraint

        is DataBaseError.Unknown ->
            R.string.error_unknown
    }
}