package com.example.safespace.feature.hub.navigation

/**
 * Navigation entry point for the Hub feature.
 *
 * Abstracts other modules from concrete routes defined in the impl module.
 */
interface HubEntryPoint {
    /** Returns the route to the hub screen. */
    fun hubRoute(): Any
}
