package com.example.safespace.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.safespace.core.domain.model.ExpenseCategory

fun ExpenseCategory.icon(): ImageVector = when (this) {
    ExpenseCategory.FOOD -> Icons.Default.Favorite
    ExpenseCategory.SHOPPING -> Icons.Default.ShoppingCart
    ExpenseCategory.HOME -> Icons.Default.Home
    ExpenseCategory.SPORT -> Icons.Default.Star
    ExpenseCategory.OTHER -> Icons.Default.Build
}


