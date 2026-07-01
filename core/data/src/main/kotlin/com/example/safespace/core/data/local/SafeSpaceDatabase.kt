package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, CalendarEventEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SafeSpaceDatabase: RoomDatabase() {

    abstract val expensesDao: ExpensesDao
    abstract val calendarDao: CalendarDao

}