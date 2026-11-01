package com.example.safespace.koin

import org.koin.core.module.Module

/**
 * Debug-сборка использует реальные дата-биндинги из `appModule`/`dataModule`,
 * оверрайдов нет.
 */
fun buildTypeKoinModules(): List<Module> = emptyList()
