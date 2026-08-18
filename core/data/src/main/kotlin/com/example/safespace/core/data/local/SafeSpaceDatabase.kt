package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.dao.IntentionDao
import com.example.safespace.core.data.local.dao.PendingExpenseDraftDao
import com.example.safespace.core.data.local.dao.WeatherDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.data.local.entity.DayWeatherEntity
import com.example.safespace.core.data.local.entity.ExpenseEntity
import com.example.safespace.core.data.local.entity.IntentionConfirmationEntity
import com.example.safespace.core.data.local.entity.IntentionTagEntity
import com.example.safespace.core.data.local.entity.PendingExpenseDraftEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CalendarEventEntity::class,
        IntentionTagEntity::class,
        IntentionConfirmationEntity::class,
        DayWeatherEntity::class,
        PendingExpenseDraftEntity::class],
    version = 7,
    exportSchema = true,
)
abstract class SafeSpaceDatabase : RoomDatabase() {

    abstract val expensesDao: ExpensesDao
    abstract val calendarDao: CalendarDao
    abstract val intentionDao: IntentionDao

    abstract val weatherDao: WeatherDao

    abstract val pendingExpenseDraftDao: PendingExpenseDraftDao

    companion object {
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
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

        // День 15 F-10 ч.2: снимок series id на трате, чтобы «обычно ~$80»
        // считался одной SQL-агрегацией по колонке (без JOIN с календарём).
        // Nullable → показ только для recurring, one-off не портит агрегат.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE expenses_table ADD COLUMN recurringEventId TEXT"
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
              CREATE TABLE IF NOT EXISTS pending_expense_drafts (
                  id TEXT NOT NULL PRIMARY KEY,                                                                                                                                    
                  amount REAL NOT NULL,
                  currency TEXT NOT NULL,                                                                                                                                          
                  merchant TEXT,                            
                  occurredAtMillis INTEGER NOT NULL,
                  sourcePackage TEXT NOT NULL,                                                                                                                                     
                  dedupeKey TEXT NOT NULL,
                  status TEXT NOT NULL,                                                                                                                                            
                  linkedExpenseId TEXT                      
              )
          """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pending_expense_drafts_status ON pending_expense_drafts(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pending_expense_drafts_dedupeKey ON pending_expense_drafts(dedupeKey)")
            }
        }
    }
}
