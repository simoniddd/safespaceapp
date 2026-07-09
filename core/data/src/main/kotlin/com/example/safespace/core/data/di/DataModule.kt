package com.example.safespace.core.data.di

import com.example.safespace.core.data.repository.IntentionRepositoryImpl
import com.example.safespace.core.data.worker.IntentionSchedulerImpl
import com.example.safespace.core.data.worker.IntentionWorker
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.worker.IntentionScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val dataModule = module {
    single<IntentionRepository> { IntentionRepositoryImpl(get()) }
    single<IntentionScheduler> { IntentionSchedulerImpl(androidContext()) }
    worker { IntentionWorker(get(), get(), get()) }
}
