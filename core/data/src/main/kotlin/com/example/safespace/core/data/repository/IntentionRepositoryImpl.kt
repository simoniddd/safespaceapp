package com.example.safespace.core.data.repository

import com.example.safespace.core.data.local.dao.IntentionDao
import com.example.safespace.core.data.local.entity.IntentionConfirmationEntity
import com.example.safespace.core.data.local.entity.IntentionTagEntity
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.domain.state.IntentionStatus
import com.example.safespace.core.domain.state.toIntentionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class IntentionRepositoryImpl(
    private val intentionDao: IntentionDao
) : IntentionRepository {
    override fun observeIntentions(): Flow<Map<String, IntentionState>> {
        return combine(
            intentionDao.observeAllTaggedIds(),
            intentionDao.observeAllConfirmations(),
        ) { taggedIds, confirmations ->
            val result = mutableMapOf<String, IntentionState>()
            taggedIds.forEach { id ->
                result[id] = IntentionState.UPCOMING
            }

            confirmations.forEach { entity ->
                if (result.containsKey(entity.eventId)) {
                    val state = entity.status.toIntentionState()
                    if (state != null) result[entity.eventId] = state
                }
            }
            result
        }
    }


    override suspend fun markEventAsIntention(eventId: String) {
        val entity = IntentionTagEntity(
            eventId = eventId
        )
        return intentionDao.insertIntentionTag(entity)
    }

    override suspend fun isEventTagged(eventId: String): Boolean {
        return intentionDao.isEventTagged(eventId)
    }

    override suspend fun insertPendingIfAbsent(eventId: String, instanceStart: Long) {
        val entity = IntentionConfirmationEntity(
            eventId = eventId,
            instanceStart = instanceStart,
            status = IntentionStatus.Pending.name,
            answeredAt = null
        )
        return intentionDao.insertConfirmIntention(entity)
    }

    override suspend fun getStatus(
        eventId: String,
        instanceStart: Long
    ): IntentionState? {
        val status = intentionDao.getIntentionStatus(
            eventId = eventId,
            instanceStart = instanceStart
        )
        return status.toIntentionState()
    }

    override suspend fun updateStatus(
        eventId: String,
        instanceStart: Long,
        newStatus: IntentionState
    ) {
        intentionDao.updateIntentionStatus(
            eventId = eventId,
            instanceStart = instanceStart,
            newStatus = newStatus.name
        )

    }
}

