package com.example.safespace.feature.expenses.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface ExpensesRoute {
    @Serializable
    data object List : ExpensesRoute
}
