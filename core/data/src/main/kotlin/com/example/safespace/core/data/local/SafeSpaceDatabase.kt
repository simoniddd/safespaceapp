package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.dao.IntentionDao
import com.example.safespace.core.data.local.dao.WeatherDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.data.local.entity.DayWeatherEntity
import com.example.safespace.core.data.local.entity.ExpenseEntity
import com.example.safespace.core.data.local.entity.IntentionConfirmationEntity
import com.example.safespace.core.data.local.entity.IntentionTagEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CalendarEventEntity::class,
        IntentionTagEntity::class,
        IntentionConfirmationEntity::class,
        DayWeatherEntity::class],
    version = 5,
    exportSchema = false
)
abstract class SafeSpaceDatabase : RoomDatabase() {

    abstract val expensesDao: ExpensesDao
    abstract val calendarDao: CalendarDao
    abstract val intentionDao: IntentionDao

    abstract val weatherDao: WeatherDao

    companion object {
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `day_weather` (
            `dateString` TEXT NOT NULL,
            `iconName` TEXT NOT NULL,
            PRIMARY KEY(`dateString`)
            )
            """.trimIndent()
                )
            }
        }
    }
}