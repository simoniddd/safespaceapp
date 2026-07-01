package com.example.safespace.feature.calendar.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface CalendarRoute {
    @Serializable
    data object Main : CalendarRoute
}
