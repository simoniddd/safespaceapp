package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.worker.IntentionScheduler

class FakeIntentionScheduler : IntentionScheduler {
    override fun scheduleIntentionCheck(eventId: String, instanceStart: Long, delayMillis: Long) = Unit
}
