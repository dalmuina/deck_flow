package com.dalmuina.di

import com.dalmuina.ui.UiEventDispatcher
import com.dalmuina.ui.UiEventDispatcherImpl
import org.koin.dsl.module

val coreUiModule = module {
    single<UiEventDispatcher> { UiEventDispatcherImpl() }
}