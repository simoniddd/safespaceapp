package com.example.safespace.core.domain.state

enum class IntentionState {
    UPCOMING, PENDING, SKIPPED, DONE

}

fun IntentionStatus.mapToIntentionState(): IntentionState {
    return when (this) {
        IntentionStatus.Upcoming -> IntentionState.UPCOMING
        IntentionStatus.Pending -> IntentionState.PENDING
        IntentionStatus.Skipped -> IntentionState.SKIPPED
        IntentionStatus.Done -> IntentionState.DONE
    }
}

fun String?.toIntentionState(): IntentionState? {
    return IntentionStatus.entries.find { it.name == this }?.mapToIntentionState()
}

enum class IntentionStatus {
    Upcoming, Pending, Skipped, Done
}