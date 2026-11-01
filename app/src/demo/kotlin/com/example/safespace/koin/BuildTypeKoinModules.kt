package com.example.safespace.koin

import com.example.safespace.demo.demoDataModule
import org.koin.core.module.Module

/**
 * Для демо-сборки добавляем `demoDataModule`, который переопределяет Clock, все
 * репозитории ленты и календарь-источник. Реальные биндинги из `dataModule`
 * сверху не удаляются — Koin разрешает override (`allowOverride` по умолчанию
 * включён в Koin 3.5+), поздний модуль побеждает.
 */
fun buildTypeKoinModules(): List<Module> = listOf(demoDataModule)
