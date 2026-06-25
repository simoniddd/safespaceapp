package com.example.safespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.expenses.presentation.navigation.expensesGraph
import com.example.safespace.ui.theme.SafeSpaceTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val expensesEntryPoint: ExpensesEntryPoint by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeSpaceTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = expensesEntryPoint.expensesRoute(),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        expensesGraph()
                    }
                }
            }
        }
    }
}
