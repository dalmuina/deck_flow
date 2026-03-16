package com.dalmuina.core.di

import com.dalmuina.core.ui.UiEventDispatcher
import com.dalmuina.core.ui.UiEventDispatcherImpl
import org.koin.dsl.module
import java.time.Clock

val coreUiModule = module {
    single<UiEventDispatcher> { UiEventDispatcherImpl() }
    single<Clock> {
        Clock.systemDefaultZone()
    }
}