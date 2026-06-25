package com.example.safespace.feature.expenses.presentation.navigation

import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint

internal class ExpensesEntryPointImpl : ExpensesEntryPoint {
    override fun expensesRoute(): Any = ExpensesRoute.List
}
