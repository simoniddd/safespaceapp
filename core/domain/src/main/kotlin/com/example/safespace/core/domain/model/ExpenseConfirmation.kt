package com.example.safespace.core.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class ExpenseConfirmation (
    val amount: Double,
    val merchant: String,
    val category: ExpenseCategory,
    val occurredAt: Instant,
    val note: String
)