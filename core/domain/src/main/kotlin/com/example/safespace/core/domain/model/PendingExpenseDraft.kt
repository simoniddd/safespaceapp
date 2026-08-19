package com.example.safespace.core.domain.model

import com.example.safespace.core.domain.model.notification.ParsedTransaction
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class PendingExpenseDraft(
    val id: String,
    val amount: Double,
    val currency: String,
    val merchant: String?,
    val occurredAt: Instant,
    val dedupeKey: String,
    val status: DraftStatus,
    val linkedExpenseId: String? = null // id из ExpenseItem после confirm
) {
    companion object {
        fun fromTransaction(pt: ParsedTransaction): PendingExpenseDraft {
            return PendingExpenseDraft(
                id = UUID.randomUUID().toString(),
                amount = pt.amount,
                currency = pt.currency,
                merchant = pt.merchant,
                occurredAt = pt.occurredAt,
                dedupeKey = computeDedupeKey(pt.amount, pt.merchant, pt.occurredAt),
                status = DraftStatus.NEW,
                linkedExpenseId = null
            )
        }

        internal fun computeDedupeKey(
            amount: Double,
            merchant: String?,
            timeStamp: Instant
        ): String {
            return "$amount|${merchant?.trim()?.lowercase() ?: "unknown"}|${
                timeStamp.toString().substring(0, 16)
            }"
        }
    }
}

enum class DraftStatus { NEW, CONFIRMED, DISMISSED }