package com.example.safespace.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class StatefulViewModel<S>(initial: S) : ViewModel() {

    private val _state = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun updateState(block: S.() -> S) {
        _state.update { it.block() }
    }
}
