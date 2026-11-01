package com.example.safespace.feature.hub.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceType

/**
 * Капитель даты, которая должна липнуть под статусбаром при скролле.
 *
 * Прозрачный фон — работает поверх верхнего скрима (`EdgeScrim.Top`). 24dp высота,
 * Space Mono капс. Отображается только когда шапка собственного дня уехала за край.
 */
@Composable
fun StickyCapline(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = SafeSpaceType.caplineDate,
            color = Color.White.copy(alpha = 0.65f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun StickyCaplinePreview() {
    PreviewTheme {
        StickyCapline(text = "ПЯТНИЦА · 4 ИЮЛЯ")
    }
}
