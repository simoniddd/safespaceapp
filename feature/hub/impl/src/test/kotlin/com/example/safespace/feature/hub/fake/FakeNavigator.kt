package com.example.safespace.feature.hub.fake

import com.example.safespace.core.presentation.navigation.NavCommand
import com.example.safespace.core.presentation.navigation.Navigator
import kotlinx.coroutines.flow.Flow

class FakeNavigator: Navigator {

    override val commands: Flow<NavCommand>
        get() = TODO("Not yet implemented")

    override fun navigateTo(route: Any) = Unit

    override fun navigateUp()  = Unit
}