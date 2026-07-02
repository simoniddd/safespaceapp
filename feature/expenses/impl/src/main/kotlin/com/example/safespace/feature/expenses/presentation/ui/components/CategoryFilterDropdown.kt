package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.ui.components.icon

@Composable
fun CategoryFilterDropdown(
    selectedFilter: ExpenseCategory?,
    onFilterSelected: (ExpenseCategory?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = selectedFilter?.displayName ?: "Все категории")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Все категории") },
                onClick = {
                    onFilterSelected(null)
                    expanded = false
                }
            )

            ExpenseCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    leadingIcon = {
                        Icon(imageVector = category.icon(), contentDescription = null)
                    },
                    onClick = {
                        onFilterSelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}