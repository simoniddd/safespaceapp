package com.example.safespace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.safespace.core.presentation.navigation.NavCommand
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.calendar.presentation.navigation.calendarGraph
import com.example.safespace.feature.expenses.presentation.navigation.expensesGraph
import com.example.safespace.feature.hub.navigation.HubEntryPoint
import com.example.safespace.feature.hub.presentation.navigation.hubGraph
import org.koin.compose.koinInject

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navigator: Navigator = koinInject()
    val hubEntryPoint: HubEntryPoint = koinInject()
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navigator.commands.collect { command ->
            when (command) {
                is NavCommand.To -> navController.navigate(command.route)
                NavCommand.Up -> navController.navigateUp()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = hubEntryPoint.hubRoute(),
        modifier = modifier
    ) {
        expensesGraph(navController)
        hubGraph(navController)
        calendarGraph(navController)
    }
}
