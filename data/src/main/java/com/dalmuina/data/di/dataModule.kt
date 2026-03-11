package com.dalmuina.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.dalmuina.data.database.AppDatabase
import com.dalmuina.data.datasource.LocalCardDataSource
import com.dalmuina.data.datasource.LocalDeckDataSource
import com.dalmuina.data.repository.LocalCardRepositoryImpl
import com.dalmuina.data.repository.LocalDeckRepositoryImpl
import com.dalmuina.data.repository.SelectedDeckRepositoryImpl
import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.SelectedDeckRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.jvm.java

val dataModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "deckflow.db"
        ).build()
    }

    single { get<AppDatabase>().cardDao() }

    single { get<AppDatabase>().deckDao() }

    single { LocalCardDataSource(get()) }

    single<LocalCardRepository> {
        LocalCardRepositoryImpl(get())
    }

    single { LocalDeckDataSource(get()) }

    single<LocalDeckRepository> {
        LocalDeckRepositoryImpl(get())
    }

    //DATASTORE
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile("settings")
            }
        )
    }

    single<SelectedDeckRepository> {
        SelectedDeckRepositoryImpl(get())
    }
}