package com.example.safespace.feature.hub.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.domain.model.ExpenseConfirmation
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.ui.components.QuestionAction
import com.example.safespace.core.ui.components.QuestionCard
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.AppColors
import com.example.safespace.core.ui.theme.AppColors.CoralRed
import com.example.safespace.core.ui.theme.AppColors.ElectricBlue
import com.example.safespace.core.ui.theme.AppColors.NeonYellow
import com.example.safespace.core.ui.theme.AppColors.PureBlack
import com.example.safespace.core.ui.theme.AppColors.SandBackground
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.state.PendingQuestion
import com.example.safespace.feature.hub.presentation.ui.component.DayHeaderWeatherIcon
import com.example.safespace.feature.hub.presentation.ui.component.LiveGlassOverlay
import com.example.safespace.feature.hub.presentation.ui.component.WeatherFan
import com.example.safespace.feature.hub.presentation.ui.timeline.EventRow
import com.example.safespace.feature.hub.presentation.ui.timeline.ExpenseRow
import com.example.safespace.feature.hub.presentation.ui.timeline.HealthDigitRow
import com.example.safespace.feature.hub.presentation.util.formatAmount
import com.example.safespace.feature.hub.presentation.util.formatDateHeader
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun HubScreen(
    viewModel: HubViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.refresh()
    }

    val healthLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.healthPermissions)) viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            viewModel.refresh()
        } else {
            launcher.launch(Manifest.permission.READ_CALENDAR)
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.refresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        // --- КОНТЕНТ (Поверх стекла) ---
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 48.dp)
            ) {
                if (state.healthState is HealthState.NoPermission) {
                    HealthConnectGhostBlock(
                        onClick = { healthLauncher.launch(viewModel.healthPermissions) }
                    )
                }
                val q = state.pendingQuestion
                when (q) {
                    is PendingQuestion.IntentionCheck -> {
                        QuestionCard(
                            question = "«${q.eventTitle}» состоялась?",
                            actions = listOf(
                                QuestionAction("Да ✓") {
                                    viewModel.confirmIntention(q.eventId, q.instanceStart)
                                },
                                QuestionAction("Не сегодня") {
                                    viewModel.skipIntention(q.eventId, q.instanceStart)
                                },
                            ),
                            quietAction = QuestionAction("позже") {
                                viewModel.postponeIntention(q.eventId, q.instanceStart)
                            },
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    is PendingQuestion.PendingExpenseCheck -> {
                        QuestionCard(
                            question = buildString {
                                append(q.merchant ?: "Без описания")
                                append(" · ")
                                append(formatAmount(q.amount, q.currency))
                                append(" — записать?")
                            },
                            actions = listOf(
                                QuestionAction("Записать") {
                                    viewModel.confirmPendingDraft(
                                        q.draftId,
                                        ExpenseConfirmation(
                                            amount = q.amount,
                                            merchant = q.merchant ?: "",
                                            category = ExpenseCategory.OTHER, // TODO F-04: inferCategory(q.merchant)
                                            occurredAt = Instant.fromEpochMilliseconds(q.occurredAt),
                                            note = "",
                                        )
                                    )
                                },
                                QuestionAction("Не трата") {
                                    viewModel.dismissPendingDraft(q.draftId)
                                },
                            ),
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    null -> Unit
                }
                TimelineSection(
                    items = state.timelineItems,
                    onMarkIntention = viewModel::markEventAsIntention,
                )
            }
            LiveGlassOverlay(
                weather = state.todayIcon,
                modifier = Modifier.matchParentSize()
            )
        }
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
    items: List<TimelineItem>,
    onMarkIntention: (eventId: String, instanceStart: Long, durationMin: Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    items.forEach { item ->
                        when (item) {
                            is TimelineItem.Expense -> ExpenseRow(item)

                            is TimelineItem.Event -> EventRow(
                                item = item,
                                onMarkIntention = {
                                    onMarkIntention(
                                        item.eventId,
                                        item.instanceStart,
                                        item.durationMin
                                    )
                                },
                            )

                            is TimelineItem.HealthDigit -> HealthDigitRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthConnectGhostBlock(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.42f)
            .drawBehind {
                drawRoundRect(
                    color = AppColors.GhostStroke,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(6.dp.toPx(), 4.dp.toPx())
                        ),
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Подключить Health Connect",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = "шаги · сон · тренировки",
                color = Color.Gray,
                fontSize = 11.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HubScreenLiquidGlassPreview() {
    PreviewTheme {
        HubScreen()
    }
}
