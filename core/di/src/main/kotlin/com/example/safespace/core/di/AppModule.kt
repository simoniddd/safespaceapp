package com.example.safespace.core.di

import androidx.room.Room
import com.example.safespace.core.data.local.SafeSpaceDatabase
import com.example.safespace.core.data.notification.FreedomNotificationParser
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Database, shared services, and other app-wide dependencies go here.
@OptIn(ExperimentalTime::class)
val appModule = module {

    // Единая точка «сейчас» в приложении. Прод = Clock.System; демо-сборка (стадия F) подменяет на DemoClock.
    single<Clock> { Clock.System }

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
                SafeSpaceDatabase.MIGRATION_6_7,
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
    single {
        get<SafeSpaceDatabase>().pendingExpenseDraftDao
    }
    factory<BankNotificationParser> {
        FreedomNotificationParser()
    }
}