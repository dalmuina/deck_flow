package com.dalmuina.di

import com.dalmuina.feature.card.presentation.reminder.ReminderWorker
import com.dalmuina.feature.card.presentation.session.CardViewModel
import com.dalmuina.feature.card.presentation.timer.TimerViewModel
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureCardModule =
    module {
        viewModelOf(::CardViewModel)
        viewModelOf(::TimerViewModel)
        workerOf(::ReminderWorker)
    }
