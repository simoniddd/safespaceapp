package com.example.safespace.feature.expenses.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.safespace.feature.expenses.presentation.ui.ExpensesScreen

// Extension для навграфбилдера
fun NavGraphBuilder.expensesGraph() {

    composable<ExpensesRoute.List> {
        ExpensesScreen()
    }
}