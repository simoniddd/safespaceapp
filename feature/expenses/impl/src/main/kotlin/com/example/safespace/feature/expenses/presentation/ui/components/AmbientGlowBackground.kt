package com.example.safespace.feature.expenses.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.theme.AppColors.CoralRed
import com.example.safespace.core.ui.theme.AppColors.ElectricBlue
import com.example.safespace.core.ui.theme.AppColors.NeonYellow

@Composable
internal fun AmbientGlowBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonYellow.copy(alpha = 0.48f), Color.Transparent),
                center = Offset(x = 100.dp.toPx(), y = 210.dp.toPx()),
                radius = 260.dp.toPx()
            ),
            center = Offset(x = 100.dp.toPx(), y = 210.dp.toPx()),
            radius = 260.dp.toPx()
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ElectricBlue.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(x = size.width - 60.dp.toPx(), y = 560.dp.toPx()),
                radius = 280.dp.toPx()
            ),
            center = Offset(x = size.width - 60.dp.toPx(), y = 560.dp.toPx()),
            radius = 280.dp.toPx()
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CoralRed.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(x = 70.dp.toPx(), y = 820.dp.toPx()),
                radius = 280.dp.toPx()
            ),
            center = Offset(x = 70.dp.toPx(), y = 820.dp.toPx()),
            radius = 280.dp.toPx()
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonYellow.copy(alpha = 0.2f), Color.Transparent),
                center = Offset(x = size.width - 80.dp.toPx(), y = size.height - 120.dp.toPx()),
                radius = 240.dp.toPx()
            ),
            center = Offset(x = size.width - 80.dp.toPx(), y = size.height - 120.dp.toPx()),
            radius = 240.dp.toPx()
        )
    }
}
