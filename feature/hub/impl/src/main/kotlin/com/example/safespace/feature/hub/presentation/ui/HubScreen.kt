package com.example.safespace.feature.hub.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.safespace.core.ui.theme.AppColors.CoralRed
import com.example.safespace.core.ui.theme.AppColors.ElectricBlue
import com.example.safespace.core.ui.theme.AppColors.NeonYellow
import com.example.safespace.core.ui.theme.AppColors.PureBlack
import com.example.safespace.core.ui.theme.AppColors.SandBackground
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HubScreen(
    viewModel: HubViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    val dailyLimit = 600.0

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.loadCalendarEvents()
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.loadCalendarEvents()
        } else {
            launcher.launch(Manifest.permission.READ_CALENDAR)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBackground)
    ) {
        // --- 1. AMBIENT GLOWS (Световые пятна на фоне) ---
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = 140.dp)
                .size(340.dp, 280.dp)
                .blur(88.dp)
                .background(NeonYellow.copy(alpha = 0.5f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = 380.dp)
                .size(280.dp)
                .blur(95.dp)
                .background(ElectricBlue.copy(alpha = 0.35f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-70).dp, y = 560.dp)
                .size(280.dp)
                .blur(100.dp)
                .background(CoralRed.copy(alpha = 0.35f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 40.dp)
                .size(260.dp)
                .blur(90.dp)
                .background(NeonYellow.copy(alpha = 0.22f), CircleShape)
        )

        // --- 2. КОНТЕНТ (Поверх стекла) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, bottom = 48.dp)
        ) {
            HubHeader(userName = "Alex")
            Spacer(modifier = Modifier.height(20.dp))
            FinancialAnchorCard(
                spent = state.dailyExpenses,
                limit = dailyLimit,
                onClick = viewModel::onExpensesClicked
            )
            Spacer(modifier = Modifier.height(14.dp))
            EcosystemWidgets()
            Spacer(modifier = Modifier.height(26.dp))
            TimelineSection(state.timelineItems)
        }
    }
}

@Composable
private fun HubHeader(
    userName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ПОНЕДЕЛЬНИК · MONDAY",
                fontSize = 10.sp,
                letterSpacing = 2.5.sp,
                color = PureBlack.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Сегодня, 24 Июня",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack,
                letterSpacing = (-0.3).sp
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Время
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.32f))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "17:00 ◆",
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = PureBlack.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace
                )
            }
            // Аватар
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF121212).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName,
                    color = NeonYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FinancialAnchorCard(
    spent: Double,
    limit: Double,
    onClick: () -> Unit
) {
    // 1. Математика прогресса
    val remaining = (limit - spent).coerceAtLeast(0.0) // Чтобы не уходило в минус
    val progress = (spent / limit).coerceIn(0.0, 1.0)
    val progressPercent = (progress * 100).toInt()

    // 2. Форматируем сумму для крупного шрифта (бьем на целые и сотые)
    val spentFormatted = "%.2f".format(spent).split(".")
    val spentMain = spentFormatted.getOrElse(0) { "0" }
    val spentDecimals = spentFormatted.getOrElse(1) { "00" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .background(Color(0xFF101010).copy(alpha = 0.58f))
            .border(1.dp, Color.White.copy(alpha = 0.13f), RoundedCornerShape(32.dp))
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ПОТРАЧЕНО СЕГОДНЯ",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = NeonYellow.copy(alpha = 0.75f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "SPENT TODAY ↗",
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = Color.White.copy(alpha = 0.35f),
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = spentMain,
                fontSize = 72.sp,
                color = NeonYellow,
                fontFamily = FontFamily.Serif,
                lineHeight = 72.sp
            )
            Text(
                text = ".${spentDecimals}",
                fontSize = 32.sp,
                color = NeonYellow.copy(alpha = 0.85f),
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Text(
            text = "$${"%.2f".format(remaining)} до лимита · limit $${"%.2f".format(limit)}",
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            color = Color.White.copy(alpha = 0.4f),
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Чипы категорий
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            FinanceCategoryChip("Еда · $180")
            FinanceCategoryChip("Транспорт · $85")
            FinanceCategoryChip("Разное · $155")
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Прогресс бар
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFD4CC00),
                                NeonYellow,
                                Color(0xFFFFFFC8).copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "${progressPercent}",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.4f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                "$${"%.2f".format(limit)} лимит",
                fontSize = 10.sp,
                color = NeonYellow.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun FinanceCategoryChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NeonYellow.copy(alpha = 0.14f))
            .border(1.dp, NeonYellow.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = NeonYellow.copy(alpha = 0.8f),
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun EcosystemWidgets() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // Календарь
        GlassWidgetCard(
            title = "Календарь",
            statusText = "→ 2 Ч.",
            statusColor = ElectricBlue,
            subtitle = "19:00 · Встреча с командой\n2 события сегодня",
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                )
            },
            rightContent = {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "19",
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Serif,
                        color = PureBlack.copy(alpha = 0.8f)
                    )
                    Text(
                        ":00",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        color = PureBlack.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        )

        // Спорт
        GlassWidgetCard(
            title = "Спорт",
            statusText = "✓ ГОТОВО",
            statusColor = Color(0xFF7A6E00),
            statusBgColor = Color(0xFFB4A600).copy(alpha = 0.2f),
            statusBorderColor = Color(0xFFB4A600).copy(alpha = 0.4f),
            subtitle = "Спина / Бицепс · 45 мин · 320 ккал",
            iconContent = {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = NeonYellow,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(SandBackground), contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 11.sp, color = PureBlack)
                    }
                }
            },
            rightContent = { Text("↗", fontSize = 13.sp, color = PureBlack.copy(alpha = 0.4f)) }
        )

        // Покупки
        GlassWidgetCard(
            title = "Покупки",
            statusText = "! СРОЧНО",
            statusColor = Color(0xFFCC3030),
            statusBgColor = CoralRed.copy(alpha = 0.2f),
            statusBorderColor = CoralRed.copy(alpha = 0.45f),
            subtitle = "",
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(CoralRed)
                )
            },
            rightContent = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CoralRed), contentAlignment = Alignment.Center
                ) {
                    Text("8", fontSize = 18.sp, color = Color.White, fontFamily = FontFamily.Serif)
                }
            },
            bottomContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 27.dp, top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF141414).copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.38f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CoralRed)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "5 / 13 куплено",
                        fontSize = 10.sp,
                        color = PureBlack.copy(alpha = 0.65f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        )

        // Медиа
        GlassWidgetCard(
            title = "Медиа",
            statusText = "ВЕЧЕРОМ",
            statusColor = PureBlack.copy(alpha = 0.7f),
            statusBgColor = PureBlack.copy(alpha = 0.09f),
            statusBorderColor = PureBlack.copy(alpha = 0.18f),
            subtitle = "",
            bgColor = Color.White.copy(alpha = 0.18f),
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.5.dp, PureBlack.copy(alpha = 0.3f), CircleShape)
                )
            },
            rightContent = { Text("↗", fontSize = 13.sp, color = PureBlack.copy(alpha = 0.4f)) },
            bottomContent = {
                Column(modifier = Modifier.padding(start = 27.dp, top = 4.dp)) {
                    Text(
                        "Дюна · стр. 240 / 360 · осталось 120",
                        fontSize = 11.sp,
                        color = PureBlack.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF141414).copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.67f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PureBlack.copy(alpha = 0.45f))
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun GlassWidgetCard(
    title: String,
    statusText: String,
    statusColor: Color,
    subtitle: String,
    iconContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    bgColor: Color = Color.White.copy(alpha = 0.26f),
    statusBgColor: Color = statusColor.copy(alpha = 0.22f),
    statusBorderColor: Color = statusColor.copy(alpha = 0.45f),
    bottomContent: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(bgColor)
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconContent()
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        color = PureBlack
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(statusBgColor)
                            .border(1.dp, statusBorderColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = statusColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = PureBlack.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
            rightContent()
        }
        bottomContent?.invoke()
    }
}

@Composable
private fun TimelineSection(
    items: List<HubTimelineItem>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Хроника дня", fontSize = 24.sp, fontFamily = FontFamily.Serif, color = PureBlack)
            Text(
                "FEED · ${items.size}",
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                color = PureBlack.copy(alpha = 0.45f),
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        ) {
            // Вертикальная линия
            Canvas(modifier = Modifier.matchParentSize()) {
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonYellow.copy(alpha = 0.45f),
                            CoralRed.copy(alpha = 0.35f),
                            ElectricBlue.copy(alpha = 0.55f),
                            PureBlack.copy(alpha = 0.12f)
                        )
                    ),
                    start = Offset(6.5.dp.toPx(), 20.dp.toPx()),
                    end = Offset(6.5.dp.toPx(), size.height - 22.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            Column {
                if (items.isEmpty()) {
                    Text(
                        text = "Лента пуста",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        color = PureBlack.copy(alpha = 0.5f)
                    )
                } else {
                    items.forEach { timelineItem ->
                        when (timelineItem) {
                            is HubTimelineItem.Expense -> {
                                val expense = timelineItem.item
                                TimelineItem(
                                    dotColor = CoralRed,
                                    time = expense.dateString,
                                    title = expense.description,
                                    subtitle = "${expense.category.uppercase()} · -$${expense.amount}",
                                    isPast = true
                                )
                            }

                            is HubTimelineItem.Event -> TODO()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    dotColor: Color,
    time: String,
    title: String,
    subtitle: String,
    isPast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(dotColor)
                .padding(top = 3.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack.copy(alpha = if (isPast) 0.6f else 1f),
                textDecoration = if (isPast) TextDecoration.LineThrough else null,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = PureBlack.copy(alpha = 0.42f),
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PureBlack.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TimelineItemActive(time: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(ElectricBlue.copy(alpha = 0.25f))
                .border(2.5.dp, ElectricBlue, CircleShape)
                .padding(top = 3.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricBlue)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "СКОРО",
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = PureBlack.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricBlue,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TimelineItemFuture(time: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .border(1.5.dp, PureBlack.copy(alpha = 0.5f), CircleShape)
                .padding(top = 3.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = PureBlack.copy(alpha = 0.35f),
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PureBlack.copy(alpha = 0.3f),
            fontFamily = FontFamily.Monospace
        )
    }
}

// Посмотри на этот Preview. Он отрендерит ровно то, что было в HTML.
@Preview(showBackground = true)
@Composable
fun HubScreenLiquidGlassPreview() {
    HubScreen()
}
