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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.ui.components.timeline.TimelineRow
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceTokens.AmberIntention
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import kotlin.math.roundToInt

/**
 * Строка события в ленте. Жанр «тише траты» — акцент только у подтверждённого
 * намерения (единственное тёплое пятно ленты).
 *
 * @param isFuture считается на месте вызова как `item.instanceStart > now`.
 *   Будущее событие — opacity 42% (включая гаттер времени).
 */
@Composable
fun EventRow(
    item: TimelineItem.Event,
    isFuture: Boolean,
    onMarkIntention: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isConfirmed = item.intentionState == IntentionState.DONE
    val rowAlpha = if (isFuture) 0.42f else 1f
    val eventHeight = eventHeightFor(item.durationMin)
    val borderColor = if (isConfirmed) AmberIntention else Color.White.copy(alpha = 0.20f)
    val borderWidth = if (isConfirmed) 2.5.dp else 2.dp

    TimelineRow(
        modifier = modifier.alpha(rowAlpha),
        gutter = {
            Text(
                text = item.time,
                style = SafeSpaceType.timeGutter,
                color = if (isConfirmed) AmberIntention.copy(alpha = 0.7f)
                else Color.White.copy(alpha = 0.28f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = eventHeight)
                    .then(if (isConfirmed) Modifier.amberGlow() else Modifier)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 12.dp, bottomStart = 3.dp, bottomEnd = 12.dp))
                    .background(SafeSpaceTokens.Glass.event),
            ) {
                // Левая акцентная грань — отдельный Box, чтобы не искажать паддинги контента.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(eventHeight)
                        .padding(start = 0.dp),
                ) {
                    Box(
                        Modifier
                            .width(borderWidth)
                            .height(eventHeight)
                            .background(borderColor),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = eventHeight)
                        .padding(start = 13.dp, end = 13.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = spacedBy(8.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (isConfirmed) {
                            Text(
                                text = "НАМЕРЕНИЕ · ВЫПОЛНЕНО",
                                style = SafeSpaceType.caplineDate,
                                color = AmberIntention.copy(alpha = 0.8f),
                            )
                            Spacer(Modifier.height(3.dp))
                        }
                        Text(text = item.title, style = SafeSpaceType.eventTitle)
                        Spacer(Modifier.height(2.dp))
                        Text(text = item.subtitle, style = SafeSpaceType.timeGutter)
                        if (item.recurringHint != null) {
                            Spacer(Modifier.height(2.dp))
                            Text(text = item.recurringHint, style = SafeSpaceType.amountLinked)
                        }
                    }
                    // Кнопка «🎯» — только пока намерение ещё не задано и событие НЕ прошло.
                    if (item.intentionState == null && isFuture) {
                        Text(
                            text = "🎯",
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onMarkIntention)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                    if (isConfirmed) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(50))
                                .background(AmberIntention.copy(alpha = 0.18f))
                                .border(1.5.dp, AmberIntention.copy(alpha = 0.55f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "✓", style = SafeSpaceType.caplineDate, color = AmberIntention)
                        }
                    }
                }
            }
        },
    )
}

/**
 * Высота события пропорциональна длительности: 56dp при ≤15 мин → 160dp при ≥3 ч.
 * Линейная интерполяция, границы включительно. Контент прижат к верху через `Top`.
 */
internal fun eventHeightFor(durationMin: Int): Dp {
    if (durationMin <= 15) return 56.dp
    if (durationMin >= 180) return 160.dp
    val span = (160 - 56).toFloat() * (durationMin - 15) / (180 - 15)
    return (56 + span.roundToInt()).dp
}

// Янтарный glow — отрисовывается как мягкий фоновый бордер + лёгкий внутренний
// highlight. Без `Modifier.shadow` (там только тёмные тени).
private fun Modifier.amberGlow(): Modifier =
    this
        .border(1.dp, AmberIntention.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
        .background(AmberIntention.copy(alpha = 0.09f), RoundedCornerShape(12.dp))

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EventRowPastPreview() {
    PreviewTheme {
        EventRow(
            item = TimelineItem.Event(
                sortKey = 0L,
                stableId = "ev-1",
                time = "10:00",
                eventId = "e1",
                instanceStart = 0L,
                durationMin = 60,
                title = "Планирование спринта",
                subtitle = "WORK · 60 мин",
                recurringHint = null,
                intentionState = null,
                doneCount = 0,
            ),
            isFuture = false,
            onMarkIntention = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EventRowConfirmedPreview() {
    PreviewTheme {
        EventRow(
            item = TimelineItem.Event(
                sortKey = 0L,
                stableId = "ev-2",
                time = "12:00",
                eventId = "e2",
                instanceStart = 0L,
                durationMin = 60,
                title = "Пробежка в обед",
                subtitle = "SPORT · 60 мин",
                recurringHint = null,
                intentionState = IntentionState.DONE,
                doneCount = 3,
            ),
            isFuture = false,
            onMarkIntention = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EventRowFuturePreview() {
    PreviewTheme {
        EventRow(
            item = TimelineItem.Event(
                sortKey = 0L,
                stableId = "ev-3",
                time = "17:00",
                eventId = "e3",
                instanceStart = Long.MAX_VALUE,
                durationMin = 60,
                title = "Созвон с командой",
                subtitle = "WORK · 60 мин",
                recurringHint = null,
                intentionState = null,
                doneCount = 0,
            ),
            isFuture = true,
            onMarkIntention = {},
        )
    }
}
