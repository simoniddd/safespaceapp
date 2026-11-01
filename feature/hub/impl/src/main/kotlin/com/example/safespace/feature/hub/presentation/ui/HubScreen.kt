@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.example.safespace.feature.hub.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.ui.components.QuestionAction
import com.example.safespace.core.ui.components.QuestionCard
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.AppColors
import com.example.safespace.core.ui.theme.SafeSpaceTokens
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.model.TimelineNode
import com.example.safespace.feature.hub.presentation.state.DayKind
import com.example.safespace.feature.hub.presentation.state.DaySection
import com.example.safespace.feature.hub.presentation.state.PendingQuestion
import com.example.safespace.feature.hub.presentation.ui.component.DayHeader
import com.example.safespace.feature.hub.presentation.ui.component.EdgeScrimBottom
import com.example.safespace.feature.hub.presentation.ui.component.EdgeScrimTop
import com.example.safespace.feature.hub.presentation.ui.component.GlassFab
import com.example.safespace.feature.hub.presentation.ui.component.LiveGlassOverlay
import com.example.safespace.feature.hub.presentation.ui.component.NowLine
import com.example.safespace.feature.hub.presentation.ui.component.StickyCapline
import com.example.safespace.feature.hub.presentation.ui.component.formatCapline
import com.example.safespace.feature.hub.presentation.ui.timeline.EventRow
import com.example.safespace.feature.hub.presentation.ui.timeline.ExpenseRow
import com.example.safespace.feature.hub.presentation.ui.timeline.HealthDigitRow
import com.example.safespace.feature.hub.presentation.ui.timeline.NestedExpenseRow
import com.example.safespace.feature.hub.presentation.util.formatAmount
import com.example.safespace.feature.hub.presentation.util.rememberNow
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val HubBackground = Color(0xFF141414)

@OptIn(ExperimentalTime::class)
@Composable
fun HubScreen(
    viewModel: HubViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) viewModel.refresh() }

    val healthLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.healthPermissions)) viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) viewModel.refresh()
        else launcher.launch(Manifest.permission.READ_CALENDAR)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.refresh()
        }
    }

    val now by rememberNow()
    val listState = rememberLazyListState()
    val todayWeather = state.days.firstOrNull { it.kind == DayKind.TODAY }?.weatherIcon

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HubBackground),
    ) {
        val statusInsets = WindowInsets.statusBars.asPaddingValues()
        val navInsets = WindowInsets.navigationBars.asPaddingValues()
        val contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = statusInsets.calculateTopPadding() + 36.dp,     // sticky capline
            bottom = navInsets.calculateBottomPadding() + 112.dp, // FAB
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            renderFeed(
                days = state.days,
                pendingQuestion = state.pendingQuestion,
                healthState = state.healthState,
                now = now,
                onMarkIntention = viewModel::markEventAsIntention,
                onConfirmIntention = viewModel::confirmIntention,
                onSkipIntention = viewModel::skipIntention,
                onPostponeIntention = viewModel::postponeIntention,
                onConfirmDraft = viewModel::confirmPendingDraft,
                onDismissDraft = viewModel::dismissPendingDraft,
                onRequestHealthPermission = { healthLauncher.launch(viewModel.healthPermissions) },
            )
        }

        LiveGlassOverlay(weather = todayWeather, modifier = Modifier.fillMaxSize())

        // Верхний скрим — лента растворяется, под ним sticky-капитель.
        EdgeScrimTop(modifier = Modifier.align(Alignment.TopCenter))

        // Sticky-капитель: ПЯТНИЦА · 4 ИЮЛЯ. Overlay-вариант — видна только когда полная
        // шапка собственного дня уехала за край. Так в мокапе двойной даты в покое нет.
        StickyCaplineOverlay(
            days = state.days,
            listState = listState,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        )

        EdgeScrimBottom(modifier = Modifier.align(Alignment.BottomCenter))

        GlassFab(
            onClick = viewModel::onExpensesClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(end = 22.dp, bottom = 26.dp),
        )
    }
}

@OptIn(ExperimentalTime::class)
internal fun LazyListScope.renderFeed(
    days: List<DaySection>,
    pendingQuestion: PendingQuestion?,
    healthState: HealthState,
    now: Instant,
    onMarkIntention: (String, Long, Int) -> Unit,
    onConfirmIntention: (String, Long) -> Unit,
    onSkipIntention: (String, Long) -> Unit,
    onPostponeIntention: (String, Long) -> Unit,
    onConfirmDraft: (String, ExpenseConfirmation) -> Unit,
    onDismissDraft: (String) -> Unit,
    onRequestHealthPermission: () -> Unit,
) {
    days.forEachIndexed { index, section ->
        val isToday = section.kind == DayKind.TODAY
        val sectionAlpha = if (isToday) 1f else 0.42f

        if (index > 0) {
            item(key = "gap-${section.date}") { Spacer(Modifier.height(16.dp)) }
        }

        item(key = "header-${section.date}") {
            // TOMORROW: заголовок приглушён на уровне самого компонента DayHeader (dayHeaderMuted
            // = 60%). Погода у TOMORROW не рисуется.
            DayHeader(
                date = section.date,
                kind = section.kind,
                weather = section.weatherIcon,
                onWeatherClick = {}, // раскрытие веера — не в скоупе бандла
            )
        }

        if (isToday && healthState is HealthState.NoPermission) {
            item(key = "hc-ghost") {
                Spacer(Modifier.height(16.dp))
                HealthConnectGhostBlock(onClick = onRequestHealthPermission)
            }
        }

        // Собираем позиции NowLine и QuestionCard только для TODAY.
        val nowMillis = now.toEpochMilliseconds()
        val lastPastIndex = if (isToday) {
            section.items.indexOfLast { it.sortKey <= nowMillis }
        } else -1

        var prevKind: RowKind? = RowKind.HEADER
        section.items.forEachIndexed { idx, node ->
            val curKind = node.toRowKind()
            val gap = gapBefore(prevKind, curKind)

            item(key = node.stableId, contentType = curKind) {
                Column(modifier = Modifier.alpha(sectionAlpha)) {
                    if (gap > 0.dp) Spacer(Modifier.height(gap))
                    RenderNode(node = node, now = now, onMarkIntention = onMarkIntention)
                }
            }
            prevKind = curKind

            // NowLine вставляется ПОСЛЕ последнего элемента с sortKey <= now.
            if (isToday && idx == lastPastIndex) {
                item(key = "now-line", contentType = RowKind.NOW) {
                    val timeLabel = remember(now) { formatTimeLabel(now) }
                    Column {
                        Spacer(Modifier.height(14.dp))
                        NowLine(timeLabel = timeLabel)
                    }
                }
                prevKind = RowKind.NOW

                // Карточка-вопрос — сразу после NowLine (где внимание).
                if (pendingQuestion != null) {
                    item(key = "question", contentType = RowKind.QUESTION) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            RenderQuestion(
                                question = pendingQuestion,
                                onConfirmIntention = onConfirmIntention,
                                onSkipIntention = onSkipIntention,
                                onPostponeIntention = onPostponeIntention,
                                onConfirmDraft = onConfirmDraft,
                                onDismissDraft = onDismissDraft,
                            )
                        }
                    }
                    prevKind = RowKind.QUESTION
                }
            }
        }

        // Если TODAY, лента пуста, но NowLine всё равно уместна «сейчас в тишине».
        if (isToday && section.items.isEmpty()) {
            item(key = "empty-today") {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Пока тихо 🌿",
                        style = SafeSpaceType.eventTitle,
                        color = Color.White.copy(alpha = 0.42f),
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderNode(
    node: TimelineNode,
    now: Instant,
    onMarkIntention: (String, Long, Int) -> Unit,
) {
    when (node) {
        is TimelineNode.Leaf -> RenderItem(node.item, now, onMarkIntention)
        is TimelineNode.EventWithExpenses -> Column {
            RenderItem(node.event, now, onMarkIntention)
            node.nested.forEach { expense ->
                Spacer(Modifier.height(5.dp))
                NestedExpenseRow(item = expense)
            }
        }
    }
}

@Composable
private fun RenderItem(
    item: TimelineItem,
    now: Instant,
    onMarkIntention: (String, Long, Int) -> Unit,
) {
    when (item) {
        is TimelineItem.Expense -> ExpenseRow(item = item)
        is TimelineItem.HealthDigit -> HealthDigitRow(item = item)
        is TimelineItem.Event -> EventRow(
            item = item,
            isFuture = item.instanceStart > now.toEpochMilliseconds(),
            onMarkIntention = {
                onMarkIntention(item.eventId, item.instanceStart, item.durationMin)
            },
        )
    }
}

@Composable
private fun RenderQuestion(
    question: PendingQuestion,
    onConfirmIntention: (String, Long) -> Unit,
    onSkipIntention: (String, Long) -> Unit,
    onPostponeIntention: (String, Long) -> Unit,
    onConfirmDraft: (String, ExpenseConfirmation) -> Unit,
    onDismissDraft: (String) -> Unit,
) {
    when (question) {
        is PendingQuestion.IntentionCheck -> QuestionCard(
            question = "«${question.eventTitle}» состоялась?",
            actions = listOf(
                QuestionAction("Да ✓") { onConfirmIntention(question.eventId, question.instanceStart) },
                QuestionAction("Не сегодня") { onSkipIntention(question.eventId, question.instanceStart) },
            ),
            quietAction = QuestionAction("позже") {
                onPostponeIntention(question.eventId, question.instanceStart)
            },
        )

        is PendingQuestion.PendingExpenseCheck -> QuestionCard(
            question = buildString {
                append(question.merchant ?: "Без описания")
                append(" · ")
                append(formatAmount(question.amount, question.currency))
                append(" — записать?")
            },
            actions = listOf(
                QuestionAction("Записать") {
                    onConfirmDraft(
                        question.draftId,
                        ExpenseConfirmation(
                            amount = question.amount,
                            merchant = question.merchant ?: "",
                            category = ExpenseCategory.OTHER,
                            occurredAt = Instant.fromEpochMilliseconds(question.occurredAt),
                            note = "",
                        ),
                    )
                },
                QuestionAction("Не трата") { onDismissDraft(question.draftId) },
            ),
        )
    }
}

@Composable
private fun StickyCaplineOverlay(
    days: List<DaySection>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val visibleCapline by remember(days) {
        derivedStateOf { visibleCaplineFor(days, listState) }
    }
    if (visibleCapline != null) {
        StickyCapline(text = visibleCapline!!, modifier = modifier)
    }
}

/**
 * Какая капитель липнет под статусбаром в данный момент:
 *  - идём с конца списка видимых элементов (которых уже «коснулся» верх экрана);
 *  - находим первую секцию, чья `header-$date` ещё НЕ видна (значит шапка уехала);
 *  - возвращаем текст капители для этой секции. null — все шапки на экране, капитель не нужна.
 */
private fun visibleCaplineFor(days: List<DaySection>, listState: LazyListState): String? {
    if (days.isEmpty()) return null
    val visibleKeys: Set<Any> = listState.layoutInfo.visibleItemsInfo
        .mapNotNull { it.key }
        .toSet()
    // Берём последнюю секцию, чья шапка уже за верхним краем.
    val uncoveredSection = days.lastOrNull { section ->
        val headerKey = "header-${section.date}"
        headerKey !in visibleKeys
    } ?: return null
    return formatCapline(uncoveredSection.date)
}

private enum class RowKind { EXPENSE, EVENT, HEALTH, NOW, QUESTION, HEADER }

private fun TimelineNode.toRowKind(): RowKind = when (this) {
    is TimelineNode.Leaf -> item.toRowKind()
    is TimelineNode.EventWithExpenses -> RowKind.EVENT
}

private fun TimelineItem.toRowKind(): RowKind = when (this) {
    is TimelineItem.Expense -> RowKind.EXPENSE
    is TimelineItem.Event -> RowKind.EVENT
    is TimelineItem.HealthDigit -> RowKind.HEALTH
}

/**
 * Отступ перед строкой — чистая функция от соседей:
 *  — вокруг NowLine/QuestionCard всегда 14dp (вставка «вне жанров»);
 *  — соседи одного жанра — 6dp (слипаются в «главу»);
 *  — разные жанры — 16dp;
 *  — после HEADER — отдельно, 16dp.
 */
private fun gapBefore(prev: RowKind?, cur: RowKind): Dp = when {
    prev == null -> 0.dp
    prev == RowKind.HEADER -> 16.dp
    prev == RowKind.NOW || cur == RowKind.NOW ||
        prev == RowKind.QUESTION || cur == RowKind.QUESTION -> 14.dp
    prev == cur -> 6.dp
    else -> 16.dp
}

@OptIn(ExperimentalTime::class)
private fun formatTimeLabel(now: Instant): String {
    val local = java.time.Instant.ofEpochMilli(now.toEpochMilliseconds())
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
    return "%02d:%02d".format(local.hour, local.minute)
}

@Composable
private fun HealthConnectGhostBlock(
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Подключить Health Connect",
                color = SafeSpaceTokens.Palette.onSurfacePrimary,
                fontSize = 14.sp,
            )
            Text(
                text = "шаги · сон · тренировки",
                color = SafeSpaceTokens.Palette.onSurfaceCaption,
                fontSize = 11.sp,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun HubScreenPreview() {
    PreviewTheme {
        HubScreen()
    }
}
