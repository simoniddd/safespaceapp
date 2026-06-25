package com.example.safespace.feature.expenses.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.components.ExpenseCategory
import com.example.safespace.feature.expenses.domain.model.ExpensesItem
import com.example.safespace.feature.expenses.presentation.action.ExpensesUiEvent
import com.example.safespace.feature.expenses.presentation.state.ExpensesState
import com.example.safespace.feature.expenses.presentation.ui.components.CategorySelector
import com.example.safespace.feature.expenses.presentation.viewmodel.ExpensesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = koinViewModel()
) {
    //подписываемся на стейтфлоу
    val state by viewModel.state.collectAsState()

    ExpensesContent(state, viewModel::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesContent(
    state: ExpensesState,
    onEvent: (ExpensesUiEvent) -> Unit // <-- Добавили лямбду для проброса событий наверх
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(ExpensesUiEvent.OnToggleAddSheet(true)) },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }


    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "SafeSpace",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "История", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (state.expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Пока нет расходов. Нажмите + чтобы добавить первую запись",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.expenses, key = { it.id }) { expense ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            val categoryEnum = ExpenseCategory.fromName(expense.category)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = categoryEnum.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)){
                                    Text(
                                        text = expense.description,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = expense.category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text (
                                    text = "${expense.amount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            if (state.isAddSheetOpened) {
                AddExpenseModalBottomSheet(
                    state = state,
                    sheetState = sheetState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseModalBottomSheet(
    state: ExpensesState,
    sheetState: SheetState,
    onEvent: (ExpensesUiEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent.invoke(ExpensesUiEvent.OnToggleAddSheet(false)) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 16.dp
                )
        ) {
            Text(
                text = "Новый расход",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onEvent.invoke(ExpensesUiEvent.OnAmountChanged(it)) },
                label = { Text("Сумма") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.inputDescription,
                onValueChange = { onEvent.invoke(ExpensesUiEvent.OnDescriptionChanged(it)) },
                label = { Text("На что потратил?") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            CategorySelector(
                selectedCategory = state.selectedCategory,
                onCategorySelected = {onEvent.invoke(ExpensesUiEvent.OnCategorySelected(it))}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onEvent.invoke(ExpensesUiEvent.OnAddExpenseClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.inputAmount.isNotBlank()
            ) {
                Text(
                    text = "Добавить"
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "LightTheme")
@Composable
private fun ExpensesScreenPreview() {
    ExpensesContent(
        state = ExpensesState(
            inputAmount = "2500",
            inputDescription = "YandexTaxi",
            expenses = listOf(
                ExpensesItem(
                    id = "1",
                    4500.0,
                    description = "Magnum (beverage)",
                    category = "Beverage",
                    dateString = "Today"
                ),
                ExpensesItem(
                    id = "2",
                    2500.0,
                    description = "Small (beverage)",
                    category = "Beverage",
                    dateString = "Yesterday"
                )
            )
        ),
        onEvent = {}
    )
}