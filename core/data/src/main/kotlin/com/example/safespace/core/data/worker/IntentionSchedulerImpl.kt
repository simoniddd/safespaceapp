package com.example.safespace.core.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.safespace.core.domain.worker.IntentionScheduler
import java.util.concurrent.TimeUnit

class IntentionSchedulerImpl(
    private val context: Context
) : IntentionScheduler {

    override fun scheduleIntentionCheck(
        eventId: String,
        instanceStart: Long,
        delayMillis: Long
    ) {
        val inputData = Data.Builder()
            .putString(KEY_EVENT_ID, eventId)
            .putLong(KEY_INSTANCE_START, instanceStart)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<IntentionWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMillis.coerceAtLeast(0), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
