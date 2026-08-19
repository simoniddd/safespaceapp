package com.example.safespace.core.data.repository

import androidx.room.withTransaction
import com.example.safespace.core.data.local.SafeSpaceDatabase
import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.dao.PendingExpenseDraftDao
import com.example.safespace.core.data.local.entity.ExpenseEntity
import com.example.safespace.core.data.local.entity.PendingExpenseDraftEntity
import com.example.safespace.core.domain.model.DraftStatus
import com.example.safespace.core.domain.model.ExpenseConfirmation
import com.example.safespace.core.domain.model.PendingExpenseDraft
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import com.example.safespace.core.domain.repository.ConfirmResult
import com.example.safespace.core.domain.repository.PendingExpenseDraftRepository
import com.example.safespace.core.domain.repository.SaveResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class PendingExpenseDraftRepositoryImpl(
    private val dao: PendingExpenseDraftDao,
    private val expensesDao: ExpensesDao,
    private val db: SafeSpaceDatabase
) : PendingExpenseDraftRepository {

    override suspend fun saveIfNew(
        pt: ParsedTransaction
    ): SaveResult {
        val draft = PendingExpenseDraft.fromTransaction(pt)
        val rowId = dao.insert(draft.toEntity())
        return if (rowId == -1L) SaveResult.Duplicate else SaveResult.Saved(draft.id)
    }


    override fun observeNew(): Flow<List<PendingExpenseDraft>> {
        return dao.observeNew().map { list ->
            list.map { it.toDomain() }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun confirm(
        draftId: String,
        edits: ExpenseConfirmation
    ): ConfirmResult = db.withTransaction {

        val draft = dao.findById(draftId)
            ?: return@withTransaction ConfirmResult.NotFound

        if (draft.status != DraftStatus.NEW.name) {
            return@withTransaction ConfirmResult.AlreadyConfirmed
        }
        val expense = edits.toEntity()
        expensesDao.insertExpense(expense)

        dao.updateStatus(
            draftId = draftId,
            newStatus = DraftStatus.CONFIRMED.name,
            linkedId = expense.id,
        )
        ConfirmResult.Success(expense.id)
    }

    override suspend fun dismiss(draftId: String) {
        dao.updateStatus(
            draftId = draftId,
            newStatus = DraftStatus.DISMISSED.name
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun PendingExpenseDraft.toEntity(): PendingExpenseDraftEntity {
    return PendingExpenseDraftEntity(
        id = id,
        amount = amount,
        currency = currency,
        merchant = merchant,
        occurredAtMillis = occurredAt.toEpochMilliseconds(),
        sourcePackage = "",
        dedupeKey = dedupeKey,
        status = status.name
    )
}

@OptIn(ExperimentalTime::class)
private fun PendingExpenseDraftEntity.toDomain(): PendingExpenseDraft {
    return PendingExpenseDraft(
        id = id,
        amount = amount,
        currency = currency,
        merchant = merchant,
        occurredAt = Instant.fromEpochMilliseconds(occurredAtMillis),
        dedupeKey = dedupeKey,
        status = DraftStatus.valueOf(status),
    )
}

@OptIn(ExperimentalTime::class)
private fun ExpenseConfirmation.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = UUID.randomUUID().toString(),
        amount = amount,
        description = if (note.isBlank()) merchant else "$merchant — $note",
        category = category.displayName,
        dateString = occurredAt.toString().substring(0, 10),
        timestamp = occurredAt.toEpochMilliseconds(),
    )
}