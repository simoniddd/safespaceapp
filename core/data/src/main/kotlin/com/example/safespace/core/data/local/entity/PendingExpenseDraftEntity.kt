package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pending_expense_drafts",
    indices = [
        Index(value = ["status"], name = "index_pending_expense_drafts_status"),
        Index(value = ["dedupeKey"], name = "index_pending_expense_drafts_dedupeKey"),
    ],
)
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