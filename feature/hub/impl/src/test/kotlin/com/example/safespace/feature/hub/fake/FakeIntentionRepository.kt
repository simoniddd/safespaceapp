package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.state.IntentionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeIntentionRepository : IntentionRepository {

    override fun observeIntentions(): Flow<Map<String, IntentionState>> = flowOf(emptyMap())

    override fun observeDoneCountByEventId(): Flow<Map<String, Int>> = flowOf(emptyMap())

    override suspend fun markEventAsIntention(eventId: String) = Unit

    override suspend fun isEventTagged(eventId: String): Boolean = false

    override suspend fun insertPendingIfAbsent(eventId: String, instanceStart: Long) = Unit

    override suspend fun getStatus(eventId: String, instanceStart: Long): IntentionState? = null

    override suspend fun updateStatus(eventId: String, instanceStart: Long, newStatus: IntentionState) = Unit
}
