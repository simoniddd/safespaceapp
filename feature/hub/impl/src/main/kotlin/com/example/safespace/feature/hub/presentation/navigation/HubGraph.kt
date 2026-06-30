package com.example.safespace.feature.hub.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.safespace.feature.hub.presentation.ui.HubScreen

fun NavGraphBuilder.hubGraph(navController: NavController) {
    composable<HubRoute.Main> {
        HubScreen()
    }
}
