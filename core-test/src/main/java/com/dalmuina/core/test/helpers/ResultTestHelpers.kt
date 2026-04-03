package com.dalmuina.core.test.helpers

import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult

fun <T> success(data: T) = DFResult.Success(data)

fun <E : DFError> failure(err: E) = DFResult.Error(err)