package com.example.safespace.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safespace.core.data.local.entity.PendingExpenseDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingExpenseDraftDao {

    @Query("SELECT * FROM pending_expense_drafts WHERE status = 'NEW' ORDER BY occurredAtMillis DESC")
    fun observeNew(): Flow<List<PendingExpenseDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(draft: PendingExpenseDraftEntity)

    @Query("UPDATE pending_expense_drafts SET status = :newStatus, linkedExpenseId = :linkedId WHERE id = :draftId")
    suspend fun updateStatus(draftId: String, newStatus: String, linkedId: String? = null)

    @Query("SELECT * FROM pending_expense_drafts WHERE dedupeKey = :key LIMIT 1")
    suspend fun findByDedupeKey(key: String): PendingExpenseDraftEntity?
}