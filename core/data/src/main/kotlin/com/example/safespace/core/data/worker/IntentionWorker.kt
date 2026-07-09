package com.example.safespace.core.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.safespace.core.domain.repository.IntentionRepository
const val KEY_EVENT_ID = "event_id"
const val KEY_INSTANCE_START = "instance_start"

class IntentionWorker(
    context: Context,
    params: WorkerParameters,
    private val intentionRepository: IntentionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getString(KEY_EVENT_ID) ?: return Result.failure()
        val instanceStart = inputData.getLong(KEY_INSTANCE_START, -1L)

        if (instanceStart == -1L) return Result.failure()

        return try {
            if (intentionRepository.isEventTagged(eventId)) {
                intentionRepository.insertPendingIfAbsent(eventId, instanceStart)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
