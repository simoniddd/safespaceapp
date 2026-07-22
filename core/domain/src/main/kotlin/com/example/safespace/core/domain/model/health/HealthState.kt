package com.example.safespace.core.domain.model.health

sealed interface HealthState {
    data object NotInstalled: HealthState
    data class NoPermission(val toRequest: Set<HealthDataType>): HealthState
    data class Available(val data: HealthDayData): HealthState
}