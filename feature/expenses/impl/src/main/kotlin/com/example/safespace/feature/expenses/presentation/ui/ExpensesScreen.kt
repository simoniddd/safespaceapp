package com.example.safespace.feature.expenses.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safespace.core.ui.theme.AppColors.NeonYellow
import com.example.safespace.core.ui.theme.AppColors.PureBlack
import com.example.safespace.core.ui.theme.AppColors.SandBackground
import com.example.safespace.feature.expenses.presentation.action.ExpensesUiEvent
import com.example.safespace.feature.expenses.presentation.ui.components.AddExpenseBottomSheet
import com.example.safespace.feature.expenses.presentation.ui.components.AmbientGlowBackground
import com.example.safespace.feature.expenses.presentation.ui.components.CategoryDietRow
import com.example.safespace.feature.expenses.presentation.ui.components.DateHeaderRow
import com.example.safespace.feature.expenses.presentation.ui.components.ExpensesHeader
import com.example.safespace.feature.expenses.presentation.ui.components.MonthlyAnchorCard
import com.example.safespace.feature.expenses.presentation.ui.components.TransactionRow
import com.example.safespace.feature.expenses.presentation.viewmodel.ExpensesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBackground)
    ) {
        AmbientGlowBackground()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item { ExpensesHeader(onBackClick = onNavigateBack) }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                MonthlyAnchorCard(totalSpent = state.totalSum)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                CategoryDietRow(categoryTotals = state.categoryTotals)
            }

            state.groupedExpenses.forEach { (date, dailyExpenses) ->
                val dailyTotal = dailyExpenses.sumOf { it.amount }
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    DateHeaderRow(date = date, dailyTotal = dailyTotal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(dailyExpenses) { expense ->
                    TransactionRow(expense = expense)
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .height(110.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, SandBackground)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 6.dp)
                    .blur(16.dp)
                    .background(NeonYellow.copy(alpha = 0.6f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(NeonYellow)
                    .clickable { viewModel.onEvent(ExpensesUiEvent.OnToggleAddSheet(true)) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Добавить расход",
                    tint = PureBlack,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (state.isAddSheetOpened) {
        AddExpenseBottomSheet(
            categories = state.categories,
            onDismiss = { viewModel.onEvent(ExpensesUiEvent.OnToggleAddSheet(false)) },
            onSave = { amount, description, category, emotion ->
                viewModel.onEvent(ExpensesUiEvent.OnAmountChanged(amount))
                viewModel.onEvent(ExpensesUiEvent.OnDescriptionChanged(description))
                if (category != null) viewModel.onEvent(ExpensesUiEvent.OnCategorySelected(category))
                viewModel.onEvent(ExpensesUiEvent.OnEmotionSelected(emotion))
                viewModel.onEvent(ExpensesUiEvent.OnAddExpenseClicked)
                viewModel.onEvent(ExpensesUiEvent.OnToggleAddSheet(false))
            },
        )
    }
}
