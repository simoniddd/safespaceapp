package com.example.safespace.core.domain.model.notification

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class ParsedTransaction(
    val amount: Double,
    val currency: String, //ISO 4217: "KZT", "USD"
    val merchant: String?,
    val occurredAt: Instant, // kotlinx-datetime
)