package com.example.safespace.core.data.di

import androidx.health.connect.client.HealthConnectClient
import com.example.safespace.core.data.repository.HealthRepositoryImpl
import com.example.safespace.core.data.repository.IntentionRepositoryImpl
import com.example.safespace.core.data.repository.WeatherRepositoryImpl
import com.example.safespace.core.data.worker.IntentionSchedulerImpl
import com.example.safespace.core.data.worker.IntentionWorker
import com.example.safespace.core.domain.repository.HealthRepository
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.repository.WeatherRepository
import com.example.safespace.core.domain.worker.IntentionScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val dataModule = module {
    single<IntentionRepository> { IntentionRepositoryImpl(get()) }
    single<IntentionScheduler> { IntentionSchedulerImpl(androidContext()) }
    worker { IntentionWorker(get(), get(), get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<HealthRepository> {
        val client = try {
            HealthConnectClient.getOrCreate(androidContext())
        } catch (e: Exception) {
            null
        }
        HealthRepositoryImpl(context = androidContext(), client = client)
    }
}
