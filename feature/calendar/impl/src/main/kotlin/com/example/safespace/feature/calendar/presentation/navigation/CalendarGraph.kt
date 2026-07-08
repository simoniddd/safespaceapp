package com.example.safespace.feature.calendar.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.safespace.feature.calendar.presentation.ui.CalendarSettingScreen
import com.example.safespace.feature.calendar.presentation.viwemodel.CalendarSettingViewmodel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.calendarGraph(navController: NavController) {
    composable<CalendarRoute.Main> {
        CalendarSettingScreen()
    }
}
