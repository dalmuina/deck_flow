package com.dalmuina.data.di

import androidx.room.Room
import com.dalmuina.data.database.AppDatabase
import com.dalmuina.data.datasource.LocalCardDataSource
import com.dalmuina.data.repository.LocalCardRepositoryImpl
import com.dalmuina.domain.LocalCardRepository
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

    single { LocalCardDataSource(get()) }

    single<LocalCardRepository> {
        LocalCardRepositoryImpl(get())
    }
}