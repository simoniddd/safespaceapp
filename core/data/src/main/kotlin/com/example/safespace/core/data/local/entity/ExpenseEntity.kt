package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses_table")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val dateString: String,
    val timestamp: Long, // для правильной сортировки времени
    val eventId: String? = null
)