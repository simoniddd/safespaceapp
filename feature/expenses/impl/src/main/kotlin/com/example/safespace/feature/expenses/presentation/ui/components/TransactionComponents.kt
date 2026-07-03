package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.ui.components.icon
import com.example.safespace.core.ui.components.toEmoji
import com.example.safespace.core.ui.theme.AppColors.CoralRed
import com.example.safespace.core.ui.theme.AppColors.ElectricBlue
import com.example.safespace.core.ui.theme.AppColors.PureBlack
import com.example.safespace.core.ui.theme.BodyFont
import com.example.safespace.core.ui.theme.MonoFont
import com.example.safespace.core.ui.theme.TitleFont

@Composable
internal fun DateHeaderRow(date: String, dailyTotal: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = date,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PureBlack,
            fontFamily = BodyFont
        )
        Text(
            text = "−$${"%.2f".format(dailyTotal)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PureBlack.copy(alpha = 0.45f),
            fontFamily = MonoFont
        )
    }
}

@Composable
internal fun TransactionRow(expense: ExpensesItem) {
    val category = ExpenseCategory.fromName(expense.category)
    val iconColor = categoryColor(category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .border(1.dp, iconColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon(),
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = expense.category.uppercase(),
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = PureBlack.copy(alpha = 0.55f),
                fontFamily = MonoFont
            )
            expense.emotion?.let { emotion ->
                Text(
                    text = emotion.toEmoji(),
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = PureBlack.copy(alpha = 0.55f),
                    fontFamily = MonoFont
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "−$${"%.2f".format(expense.amount)}",
                fontSize = 16.sp,
                color = PureBlack,
                fontFamily = TitleFont
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (expense.eventId != null) "КАЛЕНДАРЬ" else "КАРТА",
                fontSize = 10.sp,
                color = PureBlack.copy(alpha = 0.45f),
                fontFamily = MonoFont
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(Color(0xFF141414).copy(alpha = 0.08f))
    )
}

internal fun categoryColor(category: ExpenseCategory): Color = when (category) {
    ExpenseCategory.FOOD -> CoralRed
    ExpenseCategory.SPORT -> ElectricBlue
    ExpenseCategory.SHOPPING -> Color(0xFF98CF96)
    ExpenseCategory.OTHER -> Color(0xFFCAABD5)
    else -> PureBlack
}
