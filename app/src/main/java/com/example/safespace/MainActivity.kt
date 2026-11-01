package com.example.safespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.example.safespace.core.ui.theme.SafeSpaceTheme
import com.example.safespace.feature.hub.presentation.util.LocalAppClock
import com.example.safespace.navigation.AppNavigation
import org.koin.android.ext.android.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appClock = get<Clock>()
        setContent {
            SafeSpaceTheme {
                CompositionLocalProvider(LocalAppClock provides appClock) {
                    AppNavigation(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

