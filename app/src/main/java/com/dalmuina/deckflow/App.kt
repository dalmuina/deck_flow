package com.dalmuina.deckflow

import android.app.Application
import com.dalmuina.di.coreUiModule
import com.dalmuina.di.dataModule
import com.dalmuina.di.domainModule
import com.dalmuina.di.featureCardModule
import com.dalmuina.di.featureDeckModule
import com.dalmuina.di.featureStatsModule
import com.dalmuina.feature.card.presentation.reminder.ReminderScheduler
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

        startKoin {
            androidContext(this@App)
            workManagerFactory()
            modules(
                featureCardModule,
                featureDeckModule,
                featureStatsModule,
                domainModule,
                dataModule,
                coreUiModule,
            )
        }

        ReminderScheduler.scheduleDailyReminders(this)
    }
}
