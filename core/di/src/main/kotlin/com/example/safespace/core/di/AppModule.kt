package com.example.safespace.core.di

import androidx.room.Room
import androidx.room.migration.Migration
import com.example.safespace.core.data.local.SafeSpaceDatabase
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
            .addMigrations(SafeSpaceDatabase.MIGRATION_3_4, SafeSpaceDatabase.MIGRATION_3_4)
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
}
