package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, CalendarEventEntity::class],
    version = 3,
    exportSchema = false
)
abstract class SafeSpaceDatabase: RoomDatabase() {

    abstract val expensesDao: ExpensesDao
    abstract val calendarDao: CalendarDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE expenses_table ADD COLUMN emotion TEXT")
            }
        }
    }
}