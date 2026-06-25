package com.example.safespace.core.presentation.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface NavCommand {
    data class To(val route: Any) : NavCommand
    data object Up : NavCommand
}

interface Navigator {
    val commands: Flow<NavCommand>
    fun navigateTo(route: Any)
    fun navigateUp()
}

internal class NavigatorImpl : Navigator {
    private val _commands = Channel<NavCommand>(Channel.BUFFERED)
    override val commands: Flow<NavCommand> = _commands.receiveAsFlow()

    override fun navigateTo(route: Any) {
        _commands.trySend(NavCommand.To(route))
    }

    override fun navigateUp() {
        _commands.trySend(NavCommand.Up)
    }
}
