package com.example.safespace.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safespace.core.data.local.entity.IntentionConfirmationEntity
import com.example.safespace.core.data.local.entity.IntentionTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntentionTag(tag: IntentionTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConfirmIntention(confirmEntity: IntentionConfirmationEntity)

    @Query("UPDATE intention_confirmations SET status = :newStatus WHERE eventId = :eventId AND instanceStart = :instanceStart")
    suspend fun updateIntentionStatus(eventId: String, instanceStart: Long, newStatus: String)

    @Query("SELECT status FROM intention_confirmations WHERE eventId = :eventId AND instanceStart = :instanceStart")
    suspend fun getIntentionStatus(eventId: String, instanceStart: Long): String?

    @Query("SELECT eventId FROM intention_tags")
    fun observeAllTaggedIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM intention_tags WHERE eventId = :eventId)")
    suspend fun isEventTagged(eventId: String): Boolean

    @Query("SELECT * FROM intention_confirmations")
    fun observeAllConfirmations(): Flow<List<IntentionConfirmationEntity>>

}
