package com.example.safespace.core.domain.repository

import com.example.safespace.core.domain.model.ExpenseConfirmation
import com.example.safespace.core.domain.model.PendingExpenseDraft
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import kotlinx.coroutines.flow.Flow

interface PendingExpenseDraftRepository {
    suspend fun saveIfNew(pt: ParsedTransaction): SaveResult
    fun observeNew(): Flow<List<PendingExpenseDraft>>
    suspend fun confirm(draftId: String, edits: ExpenseConfirmation): ConfirmResult
    suspend fun dismiss(draftId: String)
}

sealed interface SaveResult {
    data class Saved(val draftId: String) : SaveResult
    data object Duplicate : SaveResult
}

sealed interface ConfirmResult {
    data class Success(val expenseId: String) : ConfirmResult
    data object NotFound : ConfirmResult
    data object AlreadyConfirmed : ConfirmResult
}