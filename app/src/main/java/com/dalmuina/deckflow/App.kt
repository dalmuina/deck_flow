package com.dalmuina.deckflow

import android.app.Application
import com.dalmuina.data.di.dataModule
import com.dalmuina.di.coreUiModule
import com.dalmuina.domain.di.domainModule
import com.dalmuina.feature.card.di.featureCardModule
import com.dalmuina.feature.deck.di.featureDeckModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                featureCardModule,
                featureDeckModule,
                domainModule,
                dataModule,
                coreUiModule,
            )
        }
    }
}