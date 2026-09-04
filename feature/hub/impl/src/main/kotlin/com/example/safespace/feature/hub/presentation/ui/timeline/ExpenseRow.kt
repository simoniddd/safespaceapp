package com.example.safespace.feature.hub.presentation.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.safespace.core.ui.components.timeline.TimelineRow
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseRow(
    item: TimelineItem.Expense,
    modifier: Modifier = Modifier,
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
                    .padding(top = 12.dp),
            )
        },
        content = {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SafeSpaceTokens.Glass.body)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(SafeSpaceTokens.Palette.accentYel)
                )
                Text(
                    text = item.title,
                    style = SafeSpaceType.expenseTitle,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatExpenseAmount(item.amount),
                    style = SafeSpaceType.amountExpense,
                )
            }
        },
    )
}

private val amountFormatter: NumberFormat by lazy {
    NumberFormat.getIntegerInstance(Locale("ru", "KZ"))
}

private fun formatExpenseAmount(amount: Long): String =
    "−${amountFormatter.format(amount)} ₸"


@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun ExpenseRowPreview() {
    PreviewTheme {
        ExpenseRow(
            item = TimelineItem.Expense(
                sortKey = 0L,
                time = "20:30",
                title = "Кофе",
                amount = 1250,
                stableId = ""
            )
        )
    }
}
