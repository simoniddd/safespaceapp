package com.example.safespace.feature.hub.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Живое «сейчас» для UI: тикает раз в минуту, только когда экран STARTED.
 *
 * Выровнено по границе минуты: событие в 19:00 «оживает» в 19:00:0X, а не с произвольным
 * сдвигом до 60 сек. При возврате из фона первый эмит — мгновенно свежее значение,
 * не через минуту delay'а.
 *
 * Использование:
 *   val now by rememberNow()
 *   val isFuture = event.startsAt > now
 */
@OptIn(ExperimentalTime::class)
@Composable
fun rememberNow(): State<Instant> {
    val owner = LocalLifecycleOwner.current
    return produceState(Clock.System.now(), owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                val now = Clock.System.now()
                value = now
                delay(60_000 - now.toEpochMilliseconds() % 60_000)
            }
        }
    }
}
