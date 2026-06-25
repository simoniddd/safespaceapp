package com.example.safespace.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SafeSpaceDatabase: RoomDatabase() {

    abstract val expensesDao: ExpensesDao

}