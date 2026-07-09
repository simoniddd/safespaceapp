package com.example.safespace.core.domain.worker

interface IntentionScheduler {
    fun scheduleIntentionCheck(
        eventId: String,
        instanceStart: Long,
        delayMillis: Long
    )
}