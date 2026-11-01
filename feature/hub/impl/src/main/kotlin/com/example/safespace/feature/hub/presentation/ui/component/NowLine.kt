package com.example.safespace.feature.hub.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType

/**
 * Схлопнутая линия «Сейчас»: жёлтая точка 7dp + тонкая линия 1dp, время в левой
 * колонке (40dp гаттер, как у всех остальных строк ленты).
 *
 * Expanded-состояние (тап → стеклянная полоса-сводка) — вне скоупа бандла-витрины.
 */
@Composable
fun NowLine(
    timeLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(40.dp)) {
            Text(
                text = timeLabel,
                style = SafeSpaceType.nowLineTime,
                color = SafeSpaceTokens.Palette.accentYel.copy(alpha = 0.95f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(SafeSpaceTokens.Palette.accentYel),
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.22f)),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "СЕЙЧАС",
            style = SafeSpaceType.caplineDate,
            color = Color.White.copy(alpha = 0.4f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun NowLinePreview() {
    PreviewTheme {
        NowLine(timeLabel = "15:40")
    }
}
