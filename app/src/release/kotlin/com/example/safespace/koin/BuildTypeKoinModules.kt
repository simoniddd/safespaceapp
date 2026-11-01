package com.example.safespace.koin

import org.koin.core.module.Module

/**
 * Release-сборка — реальные источники, без оверрайдов. Для демо-сборки см.
 * `app/src/demo/kotlin/.../koin/BuildTypeKoinModules.kt`.
 */
fun buildTypeKoinModules(): List<Module> = emptyList()
