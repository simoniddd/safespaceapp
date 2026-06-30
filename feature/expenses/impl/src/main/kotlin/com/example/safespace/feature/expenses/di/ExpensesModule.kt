package com.example.safespace.feature.expenses.di

import com.example.safespace.feature.expenses.data.repository.ExpensesRepositoryImpl
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.expenses.presentation.viewmodel.ExpensesViewModel
import com.example.safespace.feature.expenses.presentation.navigation.ExpensesEntryPointImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val expensesModule = module {
    single<ExpensesEntryPoint> { ExpensesEntryPointImpl() }
    single<ExpensesRepository> { ExpensesRepositoryImpl(get()) }
    viewModel { ExpensesViewModel(get(), get()) }
}
