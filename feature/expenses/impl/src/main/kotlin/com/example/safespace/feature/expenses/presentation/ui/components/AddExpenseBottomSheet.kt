package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.components.ExpenseCategory
import com.example.safespace.core.ui.theme.AppColors.NeonYellow
import com.example.safespace.core.ui.theme.AppColors.PureBlack
import com.example.safespace.core.ui.theme.AppColors.SandBackground
import com.example.safespace.feature.expenses.presentation.action.ExpensesUiEvent
import com.example.safespace.feature.expenses.presentation.state.ExpensesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddExpenseBottomSheet(
    state: ExpensesState,
    onEvent: (ExpensesUiEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(ExpensesUiEvent.OnToggleAddSheet(false)) },
        containerColor = SandBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = PureBlack.copy(alpha = 0.2f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Новый расход",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.inputAmount,
                onValueChange = { onEvent(ExpensesUiEvent.OnAmountChanged(it)) },
                label = { Text("Сумма ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureBlack,
                    unfocusedBorderColor = PureBlack.copy(alpha = 0.2f),
                    focusedLabelColor = PureBlack
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.inputDescription,
                onValueChange = { onEvent(ExpensesUiEvent.OnDescriptionChanged(it)) },
                label = { Text("На что потратил?") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureBlack,
                    unfocusedBorderColor = PureBlack.copy(alpha = 0.2f),
                    focusedLabelColor = PureBlack
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Категория",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PureBlack
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExpenseCategory.entries.toTypedArray()) { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { onEvent(ExpensesUiEvent.OnCategorySelected(category)) },
                        label = { Text(category.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonYellow,
                            selectedLabelColor = PureBlack,
                            selectedLeadingIconColor = PureBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = state.selectedCategory == category,
                            borderColor = PureBlack.copy(alpha = 0.1f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onEvent(ExpensesUiEvent.OnAddExpenseClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureBlack,
                    contentColor = Color.White
                )
            ) {
                Text("Сохранить", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
