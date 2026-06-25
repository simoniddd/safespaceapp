package com.example.safespace.feature.expenses.navigation

/**
 * Navigation entry point for the Expenses feature.
 *
 * Abstracts other modules from concrete routes defined in the impl module.
 */
interface ExpensesEntryPoint {
    /** Returns the route to the expenses list screen. */
    fun expensesRoute(): Any
}
