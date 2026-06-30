package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.theme.AppColors.NeonYellow
import com.example.safespace.core.ui.theme.MonoFont
import com.example.safespace.core.ui.theme.TitleFont

@Composable
internal fun MonthlyAnchorCard(totalSpent: Double) {
    val parts = "%.2f".format(totalSpent).split(".")
    val mainPart = parts.getOrElse(0) { "0" }
    val decimalPart = parts.getOrElse(1) { "00" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(Color(0xFF141414).copy(alpha = 0.65f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(36.dp))
            .padding(28.dp)
    ) {
        Text(
            text = "ИЮНЬ 2026",
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = Color.White.copy(alpha = 0.6f),
            fontFamily = MonoFont
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "−$$mainPart",
                fontSize = 48.sp,
                color = NeonYellow,
                fontFamily = TitleFont,
                lineHeight = 48.sp,
                letterSpacing = (-1).sp
            )
            Text(
                text = ".$decimalPart",
                fontSize = 24.sp,
                color = NeonYellow.copy(alpha = 0.85f),
                fontFamily = TitleFont,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Остаток лимита: $550.00",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontFamily = MonoFont
        )
    }
}
