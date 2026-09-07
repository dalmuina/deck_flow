package com.dalmuina.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.dalmuina.core.data.helpers.CrashlyticsLogger
import com.dalmuina.core.data.helpers.FirebaseCrashlyticsLogger
import com.dalmuina.data.database.AppDatabase
import com.dalmuina.data.datasource.DataStoreDeckDataSource
import com.dalmuina.data.datasource.DataStoreTimerDataSource
import com.dalmuina.data.datasource.RoomCardDatasource
import com.dalmuina.data.datasource.RoomDeckDataSource
import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.TimerDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.jvm.java

val dataModule =
    module {

        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    "deckflow.db",
                ).build()
        }

        single { get<AppDatabase>().cardDao() }
        single { get<AppDatabase>().deckDao() }

        single<CrashlyticsLogger> { FirebaseCrashlyticsLogger() }

        single<CardLocalDataSource> { RoomCardDatasource(get(), get()) }
        single<DeckLocalDataSource> { RoomDeckDataSource(get(), get()) }

        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.create(
                produceFile = { androidContext().preferencesDataStoreFile("settings") },
            )
        }

        single<SelectedDeckDataSource> { DataStoreDeckDataSource(get(), get()) }
        single<TimerDataSource> { DataStoreTimerDataSource(get(), get()) }
    }
