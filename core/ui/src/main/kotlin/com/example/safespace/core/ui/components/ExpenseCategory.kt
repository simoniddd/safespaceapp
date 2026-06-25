package com.example.safespace.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val displayName: String,
    val icon: ImageVector
) {
    FOOD("Еда", Icons.Default.Favorite),
    SHOPPING("Покупки", Icons.Default.ShoppingCart),
    HOME("Дом", Icons.Default.Home),
    SPORT("Спорт", Icons.Default.Star),
    OTHER("Разное", Icons.Default.Build);

    companion object {
        fun fromName(name: String): ExpenseCategory {
            return entries.find { it.displayName == name } ?: OTHER
        }
    }
}