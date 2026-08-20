package com.example.safespace.feature.hub.di

import com.example.safespace.core.domain.usecase.AutoConfirmIntentionsUseCase
import com.example.safespace.feature.hub.navigation.HubEntryPoint
import com.example.safespace.feature.hub.presentation.navigation.HubEntryPointImpl
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val hubModule = module {
    single<HubEntryPoint> { HubEntryPointImpl() }
    single { AutoConfirmIntentionsUseCase() }
    viewModel {
        HubViewModel(
            expensesRepository = get(),
            expensesEntryPoint = get(),
            calendarEntryPoint = get(),
            eventsSource = get(),
            calendarSource = get(),
            intentionRepository = get(),
            intentionScheduler = get(),
            weatherRepository = get(),
            healthRepository = get(),
            pendingExpenseDraftRepository = get(),
            autoConfirmIntentionsUseCase = get(),
            navigator = get(),
        )
    }
}
