package com.dalmuina.di

import com.dalmuina.UiEventDispatcher
import com.dalmuina.UiEventDispatcherImpl
import org.koin.dsl.module

val coreUiModule = module {
    single<UiEventDispatcher> { UiEventDispatcherImpl() }
}