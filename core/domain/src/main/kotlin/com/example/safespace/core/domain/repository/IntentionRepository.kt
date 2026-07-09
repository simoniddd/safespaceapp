package com.example.safespace.core.domain.repository

import com.example.safespace.core.domain.state.IntentionState
import kotlinx.coroutines.flow.Flow

interface IntentionRepository {

    //для ленты - реагирует когда ответил на карточку -> лента обновилась
    fun observeIntentions(): Flow<Map<String, IntentionState>>

    //для экрана деталей - пометить событие как намерение
    suspend fun markEventAsIntention(eventId: String)

    suspend fun isEventTagged(eventId: String): Boolean

    //методы для воркера чтобы вставить PENDING и обновить статус после ответа
    suspend fun insertPendingIfAbsent(eventId: String, instanceStart: Long)

    suspend fun getStatus(eventId: String, instanceStart: Long): IntentionState?

    suspend fun updateStatus(eventId: String, instanceStart: Long, newStatus: IntentionState)
}

