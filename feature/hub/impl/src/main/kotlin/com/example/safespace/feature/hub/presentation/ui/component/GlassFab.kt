package com.example.safespace.feature.hub.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens

/**
 * FAB для открытия шторки ввода траты. 56dp, стекло 9%, белый «+».
 *
 * Backdrop-blur: в проекте нет Haze (единственная библиотека, дающая настоящий
 * размыв фона в Compose без ручной работы с рендерскриптом). Делаю стекло 9%
 * + тонкий контур — визуально близко к мокапу, blur при желании владельца
 * докрутится отдельным PR с подключением Haze.
 */
@Composable
fun GlassFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SafeSpaceTokens.Glass.fab)
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun GlassFabPreview() {
    PreviewTheme {
        GlassFab(onClick = {})
    }
}
