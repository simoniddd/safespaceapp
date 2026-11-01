package com.example.safespace.feature.hub.presentation.ui.timeline

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.components.timeline.TimelineRow
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import java.text.NumberFormat
import java.util.Locale

/**
 * Вложенная трата под шапкой события. Отличается от обычной:
 *  - рисуется в слоте content внутри EventWithExpenses, с индентом слева;
 *  - тоньше (стекло 5%), сумма приглушена (`amountLinked` вместо `amountExpense`);
 *  - слева тонкий 2dp коннектор к родителю, как в мокапе.
 *
 * Никаких сводок, счётчиков и «обычно ~$80» — синергия вырезана из v1.0.
 */
@Composable
fun NestedExpenseRow(
    item: TimelineItem.Expense,
    modifier: Modifier = Modifier,
) {
    TimelineRow(
        modifier = modifier,
        gutter = {}, // время не рисуем — у вложенных трат связь с событием важнее времени
        content = {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Коннектор к родительскому событию — вертикальная чёрточка 2dp
                // (янтарь при CONFIRMED — слишком узко; оставляем нейтральный).
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(SafeSpaceTokens.AmberIntention.copy(alpha = 0.22f)),
                )
                Spacer(Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SafeSpaceTokens.Glass.expense.copy(alpha = 0.5f))
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(SafeSpaceTokens.Palette.accentYel.copy(alpha = 0.55f)),
                    )
                    Text(
                        text = item.title,
                        style = SafeSpaceType.expenseTitle.copy(color = Color.White.copy(alpha = 0.55f)),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = formatNestedAmount(item.amount),
                        style = SafeSpaceType.amountLinked,
                    )
                }
            }
        },
    )
}

private val amountFormatter: NumberFormat by lazy {
    NumberFormat.getIntegerInstance(Locale("ru", "KZ"))
}

private fun formatNestedAmount(amount: Long): String =
    "−${amountFormatter.format(amount)} ₸"

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun NestedExpenseRowPreview() {
    PreviewTheme {
        Column {
            NestedExpenseRow(
                item = TimelineItem.Expense(
                    sortKey = 0L,
                    stableId = "exp-1",
                    time = "19:30",
                    title = "Смузи после",
                    amount = 680,
                ),
            )
        }
    }
}
