package com.dalmuina.core.test.data

import com.dalmuina.domain.model.DataBaseError

object ErrorTestData {

    val unknown = DataBaseError.Unknown(RuntimeException("Unknown database error"))

}