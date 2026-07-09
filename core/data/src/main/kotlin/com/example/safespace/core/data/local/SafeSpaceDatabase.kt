package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.dao.IntentionDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.data.local.entity.ExpenseEntity
import com.example.safespace.core.data.local.entity.IntentionConfirmationEntity
import com.example.safespace.core.data.local.entity.IntentionTagEntity

@Database(
    entities = [ExpenseEntity::class, CalendarEventEntity::class, IntentionTagEntity::class, IntentionConfirmationEntity::class],
    version = 4,
    exportSchema = false
)
abstract class SafeSpaceDatabase : RoomDatabase() {

    abstract val expensesDao: ExpensesDao
    abstract val calendarDao: CalendarDao

    abstract val intentionDao: IntentionDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `intention_confirmations` (
                `eventId` TEXT NOT NULL,
                `instanceStart` INTEGER,
                `status` TEXT,
                `answeredAt` INTEGER,
                PRIMARY KEY(`eventId`, `instanceStart`))
            """.trimIndent()
                )
                database.execSQL("CREATE TABLE IF NOT EXISTS `intention_tags` (`eventId` TEXT, PRIMARY KEY(`eventId`))".trimIndent())
            }
        }
    }
}