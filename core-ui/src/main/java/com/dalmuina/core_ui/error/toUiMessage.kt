package com.dalmuina.core_ui.error

import com.dalmuina.core_ui.R
import com.dalmuina.domain.model.DataBaseError

fun DataBaseError.toUiMessage(): Int {
    return when (this) {

        DataBaseError.ConstraintViolation ->
            R.string.error_constraint

        is DataBaseError.Unknown ->
            R.string.error_unknown
    }
}
