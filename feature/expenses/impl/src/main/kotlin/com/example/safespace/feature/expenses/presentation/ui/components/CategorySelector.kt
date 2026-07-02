package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.ui.components.icon

@Composable
fun CategorySelector(
    selectedCategory: ExpenseCategory, onCategorySelected: (ExpenseCategory) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ExpenseCategory.entries) { category ->
            FilterChip(selected = selectedCategory == category, onClick = {
                onCategorySelected(category)
            }, label = { Text(category.displayName) }, leadingIcon = {
                Icon(
                    imageVector = category.icon(), contentDescription = null
                )
            })
        }
    }
}