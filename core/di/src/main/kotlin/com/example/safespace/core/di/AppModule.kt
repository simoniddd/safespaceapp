package com.example.safespace.core.di

import androidx.room.Room
import com.example.safespace.core.data.local.SafeSpaceDatabase
import com.example.safespace.core.data.notification.FreedomNotificationParser
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Database, shared services, and other app-wide dependencies go here.
val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            SafeSpaceDatabase::class.java,
            "safespace_db"
        )
            .fallbackToDestructiveMigration(false)
            .addMigrations(
                SafeSpaceDatabase.MIGRATION_4_5,
                SafeSpaceDatabase.MIGRATION_5_6,
            )
            .build()
    }

    single {
        get<SafeSpaceDatabase>().expensesDao
    }
    single {
        get<SafeSpaceDatabase>().calendarDao
    }
    single {
        get<SafeSpaceDatabase>().intentionDao
    }
    single {
        get<SafeSpaceDatabase>().weatherDao
    }
    factory<BankNotificationParser> {
        FreedomNotificationParser()
    }
}