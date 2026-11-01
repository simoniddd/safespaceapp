package com.example.safespace.feature.hub.presentation.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.components.SourceStamp
import com.example.safespace.core.ui.components.timeline.TimelineRow
import com.example.safespace.core.ui.components.toSourceLabel
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem

/**
 * Health-строка: стекло 4.5%, вместо цветной точки — круглая «печать».
 *
 * Для тренировок с известным `source` слева рисуется [SourceStamp] (26dp
 * текстовая капитель — STRAVA / GARMIN / HC / ...). Для sleep/steps источник
 * не хранится (это агрегаты Health Connect), слева кружок с эмодзи — та же
 * геометрия 26dp, визуально согласован с SourceStamp.
 */
@Composable
fun HealthDigitRow(
    item: TimelineItem.HealthDigit,
    modifier: Modifier = Modifier,
) {
    TimelineRow(
        modifier = modifier,
        gutter = {},
        content = {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SafeSpaceTokens.Glass.body)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.source != null) {
                    SourceStamp(label = item.source.toSourceLabel())
                } else {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.09f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = item.icon, fontSize = 12.sp)
                    }
                }

                Text(
                    text = item.title,
                    style = SafeSpaceType.expenseTitle,
                    modifier = Modifier.weight(1f),
                )
                Text(text = item.digit, style = SafeSpaceType.healthDigit)
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun HealthDigitRowSleepPreview() {
    PreviewTheme {
        HealthDigitRow(
            item = TimelineItem.HealthDigit(
                sortKey = 0L,
                stableId = "sleep",
                icon = "😴",
                title = "Сон",
                digit = "7ч 20м",
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun HealthDigitRowExercisePreview() {
    PreviewTheme {
        HealthDigitRow(
            item = TimelineItem.HealthDigit(
                sortKey = 0L,
                stableId = "ex",
                icon = "🏃",
                title = "Бег 5.2 км",
                digit = "32 мин",
                source = "com.strava",
            ),
        )
    }
}
