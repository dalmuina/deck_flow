package com.dalmuina.domain.di

import com.dalmuina.domain.usecase.CompleteCardUseCase
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.DeleteDeckUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetCardByIdUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.PostponeCardUseCase
import com.dalmuina.domain.usecase.SaveCardUseCase
import com.dalmuina.domain.usecase.SetSelectedDeckUseCase
import com.dalmuina.domain.usecase.UpdateCardUseCase
import com.dalmuina.domain.usecase.SetDeckCardsUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {

    single<CoroutineDispatcher>(named("IO")) { Dispatchers.IO }

    factory {
        SaveCardUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        UpdateCardUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        GetAllCardsUseCase(
            repository = get(),
        )
    }

    factory {
        CreateDeckUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        UpdateDeckNameUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        GetAllDecksUseCase(
            repository = get(),
        )
    }

    factory {
        GetCardByIdUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        DeleteCardUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        DeleteDeckUseCase(
            repository = get(),
            selectedDeckRepository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        GetSelectedDeckUseCase(
            repository = get(),
        )
    }

    factory {
        SetSelectedDeckUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        GetDeckByIdUseCase(
            repository = get(),
            clock = get(),
        )
    }

    factory {
        CompleteCardUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        PostponeCardUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }

    factory {
        SetDeckCardsUseCase(
            repository = get(),
            dispatcher = get(named("IO")),
        )
    }
}