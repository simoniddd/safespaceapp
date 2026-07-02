package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.ui.theme.AccentRed
import com.example.safespace.core.ui.theme.AccentYel
import com.example.safespace.core.ui.theme.Ink
import com.example.safespace.core.ui.theme.SheetBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    categories: List<ExpenseCategory>,
    onDismiss: () -> Unit,
    onSave: (amount: String, description: String, category: ExpenseCategory?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetBg,
        scrimColor = Color(0xFF121212).copy(alpha = 0.38f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { SheetHandle() },
    ) {
        Box {
            GlowBlob(
                color = AccentYel, alpha = 0.32f, blur = 70.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 24.dp)
                    .size(200.dp, 150.dp)
            )
            GlowBlob(
                color = AccentRed, alpha = 0.18f, blur = 70.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 40.dp)
                    .size(180.dp, 140.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 40.dp)
            ) {
                // Шапка
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "РАСХОД",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif, fontSize = 10.sp, letterSpacing = 2.5.sp,
                            color = Ink.copy(alpha = 0.45f),
                        )
                    )
                    Text(
                        "Новый расход",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp, letterSpacing = (-0.3).sp, color = Ink,
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Ink.copy(alpha = 0.10f), CircleShape)
                            .border(1.dp, Ink.copy(alpha = 0.10f), CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", fontSize = 13.sp, color = Ink)
                    }
                }

                // Сумма
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.32f), RoundedCornerShape(26.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.65f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    FieldLabel("Сумма · Amount")
                    Spacer(Modifier.height(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Serif, fontSize = 60.sp, color = Ink,
                            textAlign = TextAlign.Center,
                        ),
                        cursorBrush = SolidColor(AccentYel),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            Box(contentAlignment = Alignment.Center) {
                                if (amount.isEmpty()) {
                                    Text(
                                        "\$0.00", style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 60.sp,
                                            color = Ink.copy(alpha = 0.22f),
                                            textAlign = TextAlign.Center,
                                        )
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Описание
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("≣", fontSize = 15.sp, color = Ink.copy(alpha = 0.35f))
                    Spacer(Modifier.width(11.dp))
                    BasicTextField(
                        value = description,
                        onValueChange = { description = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium,
                            fontSize = 14.sp, color = Ink,
                        ),
                        cursorBrush = SolidColor(AccentYel),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (description.isEmpty()) {
                                Text(
                                    "На что потратил?", style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Ink.copy(alpha = 0.28f),
                                    )
                                )
                            }
                            inner()
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Категории
                FieldLabel("Категория")
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.forEach { cat ->
                        CategoryChip(
                            category = cat,
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Кнопка «Сохранить»
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Ink, RoundedCornerShape(20.dp))
                        .clickable { onSave(amount, description, selectedCategory) }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Сохранить",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif, fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp, letterSpacing = 0.8.sp, color = AccentYel,
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Вспомогательные composable
// ─────────────────────────────────────────────────────────────────────
@Composable
private fun SheetHandle() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(40.dp)
                .height(5.dp)
                .background(Ink.copy(alpha = 0.18f), RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = FontFamily.Serif, fontSize = 10.sp, letterSpacing = 2.sp,
            color = Ink.copy(alpha = 0.45f),
        )
    )
}

@Composable
private fun CategoryChip(
    category: ExpenseCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) AccentYel else Color.White.copy(alpha = 0.28f)
    val borderColor = if (selected) AccentYel else Color.White.copy(alpha = 0.55f)
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            category.displayName,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 12.sp, color = Ink,
            )
        )
    }
}

/** Мягкое «свечение» — радиальный градиент + blur */
@Composable
private fun GlowBlob(
    color: Color,
    alpha: Float,
    blur: androidx.compose.ui.unit.Dp,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .blur(blur)
            .background(
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                    center = Offset.Unspecified,
                )
            )
    )
}