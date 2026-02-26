package com.dalmuina.domain.di

import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.SaveCardUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {

    single<CoroutineDispatcher>(named("IO")) { Dispatchers.IO }

    factory {
        SaveCardUseCase(
            repository = get(),
            dispatcher = get(named("IO"))
        )
    }

    factory {
        GetAllCardsUseCase(
            repository = get(),
        )
    }
}