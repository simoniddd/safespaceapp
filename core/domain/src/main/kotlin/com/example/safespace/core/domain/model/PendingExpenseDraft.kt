package com.example.safespace.core.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class PendingExpenseDraft(
    val id: String, //UUID standard
    val amount: Double,
    val currency: String,
    val merchant: String?,
    val occurredAt: Instant,
    val sourcePackage: String, // для дебага
    val dedupeKey: String, //TODO: dedup
    val status: DraftStatus,
    val linkedExpenseId: String? = null // id из ExpenseItem после confirm
)

enum class DraftStatus { NEW, CONFIRMED, DISMISSED }