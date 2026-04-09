package com.dalmuina.di

import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.presentation.events.UiEventDispatcherImpl
import org.koin.dsl.module
import java.time.Clock

val coreUiModule = module {
    single<UiEventDispatcher> { UiEventDispatcherImpl() }
    single<Clock> {
        Clock.systemDefaultZone()
    }
}