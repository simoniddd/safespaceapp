package com.example.safespace.feature.hub.presentation.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.ui.components.timeline.TimelineRow
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem

@Composable
fun EventRow(
    item: TimelineItem.Event,
    onMarkIntention: () -> Unit,
    modifier: Modifier = Modifier
) {
    TimelineRow(
        modifier = modifier,
        gutter = {
            Text(
                text = item.time,
                style = SafeSpaceType.timeGutter,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        },
        content = {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SafeSpaceTokens.Glass.body)  // единая карточка с ExpenseRow
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                horizontalArrangement = spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                // Жанровая точка — синяя (события)
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6))
                )
                Column(modifier = Modifier.weight(1f)) {
                    // Первая строка: title + бейдж состояния
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = spacedBy(8.dp),
                    ) {
                        Text(
                            text = item.title,
                            style = SafeSpaceType.expenseTitle,   // тот же title-стиль что у Expense
                        )
                        IntentionBadge(
                            state = item.intentionState,
                            doneCount = item.doneCount,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    // Subtitle: "TYPE · N мин · N тр · $X · Yч"
                    Text(
                        text = item.subtitle,
                        style = SafeSpaceType.timeGutter,
                    )
                    if (item.recurringHint != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.recurringHint,
                            style = SafeSpaceType.amountLinked,
                        )
                    }
                }
                // Кнопка «🎯» справа если intention ещё не задан
                if (item.intentionState == null) {
                    Text(
                        text = "🎯",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onMarkIntention)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    )
}

@Composable
private fun IntentionBadge(state: IntentionState?, doneCount: Int) {
    when (state) {
        IntentionState.DONE -> Text(
            text = if (doneCount > 1) "✓ $doneCount раза" else "✓",
            style = TODO("SafeSpaceType.badge или что-нибудь мелкое monospace"),
        )

        IntentionState.UPCOMING -> Text(text = "🎯")
        else -> Unit
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EventRowPreview() {
    PreviewTheme {
        EventRow(
            item = TimelineItem.Event(
                sortKey = 0L,
                time = "19:00",
                eventId = "e1",
                instanceStart = 0L,
                durationMin = 60,
                title = "Встреча с командой",
                subtitle = "WORK · 60 мин · 2 тр · \$45 · 1ч",
                recurringHint = "обычно ~\$80",
                intentionState = null,
                doneCount = 0,
            ),
            onMarkIntention = {},
        )
    }
}