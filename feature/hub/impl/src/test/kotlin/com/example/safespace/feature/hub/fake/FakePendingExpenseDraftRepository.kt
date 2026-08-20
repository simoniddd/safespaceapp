package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.ExpenseConfirmation
import com.example.safespace.core.domain.model.PendingExpenseDraft
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import com.example.safespace.core.domain.repository.ConfirmResult
import com.example.safespace.core.domain.repository.PendingExpenseDraftRepository
import com.example.safespace.core.domain.repository.SaveResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class FakePendingExpenseDraftRepository : PendingExpenseDraftRepository {

    // контролируем из теста что «лежит в базе» pending drafts
    private val _drafts = MutableStateFlow<List<PendingExpenseDraft>>(emptyList())

    // журнал вызовов для assert'ов в тестах VM
    val savedTransactions = mutableListOf<ParsedTransaction>()
    val confirmedDrafts = mutableListOf<Pair<String, ExpenseConfirmation>>()
    val dismissedDrafts = mutableListOf<String>()

    fun emit(drafts: List<PendingExpenseDraft>) {
        _drafts.value = drafts
    }

    override fun observeNew(): Flow<List<PendingExpenseDraft>> = _drafts

    override suspend fun saveIfNew(pt: ParsedTransaction): SaveResult {
        savedTransactions += pt
        return SaveResult.Saved(draftId = UUID.randomUUID().toString())
    }

    override suspend fun confirm(draftId: String, edits: ExpenseConfirmation): ConfirmResult {
        confirmedDrafts += draftId to edits
        return ConfirmResult.Success(expenseId = UUID.randomUUID().toString())
    }

    override suspend fun dismiss(draftId: String) {
        dismissedDrafts += draftId
    }
}
