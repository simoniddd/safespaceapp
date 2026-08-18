package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_expense_drafts")
data class PendingExpenseDraftEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val currency: String,
    val merchant: String?,
    val occurredAtMillis: Long,
    val sourcePackage: String,
    val dedupeKey: String,
    val status: String,
    val linkedExpenseId: String? = null
)