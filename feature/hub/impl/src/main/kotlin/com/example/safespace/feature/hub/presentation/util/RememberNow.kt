package com.example.safespace.feature.hub.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * CompositionLocal для часов приложения.
 *
 * Внедряется в `MainActivity` из Koin (прод = `Clock.System`, демо-сборка = `DemoClock`).
 * Default — `Clock.System`, чтобы превью и несвязанные с DI composable'ы не падали.
 */
@OptIn(ExperimentalTime::class)
val LocalAppClock = staticCompositionLocalOf<Clock> { Clock.System }

/**
 * Живое «сейчас» для UI: тикает раз в минуту, только когда экран STARTED.
 *
 * Берёт [Clock] из [LocalAppClock], а не напрямую `Clock.System` — иначе demo-режим
 * не может подменить время. Выровнено по границе минуты: событие в 19:00 «оживает»
 * в 19:00:0X, а не с произвольным сдвигом до 60 сек. При возврате из фона первый
 * эмит — мгновенно свежее значение, не через минуту delay'а.
 *
 * Использование:
 *   val now by rememberNow()
 *   val isFuture = event.startsAt > now
 */
@OptIn(ExperimentalTime::class)
@Composable
fun rememberNow(): State<Instant> {
    val owner = LocalLifecycleOwner.current
    val clock = LocalAppClock.current
    return produceState(clock.now(), owner, clock) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                val now = clock.now()
                value = now
                delay(60_000 - now.toEpochMilliseconds() % 60_000)
            }
        }
    }
}
