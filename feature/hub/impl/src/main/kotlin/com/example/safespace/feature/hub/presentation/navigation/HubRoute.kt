package com.example.safespace.feature.hub.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface HubRoute {
    @Serializable
    data object Main : HubRoute
}
