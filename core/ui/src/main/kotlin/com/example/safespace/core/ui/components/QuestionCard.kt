package com.example.safespace.core.ui.components

/**
 * КАРТОЧКА-ВОПРОС — единый reusable-компонент «голоса SafeSpace».
 *
 * Канон (утверждён, итерация 2 слоя «Состояние»):
 *  - стекло 3.5% — ТИШЕ блоков данных (5.5–7%): голос, не факт дня
 *  - контур чуть заметнее, чем у блоков — единственный признак «это спрашивает приложение»
 *  - БЕЗ времени и таймрейла: вопрос вне хронологии (в LazyColumn item —
 *    без левого поля под время, отступ 14dp сверху и снизу от соседей)
 *  - надзаголовок «SAFESPACE» капителью
 *  - основные ответы РАВНОПРАВНЫ: одинаковые стеклянные кнопки, нет «правильного»
 *  - «позже»/«не сейчас» — тихий текст третьего уровня, НЕ кнопка
 *  - после ответа карточка исчезает БЕЗ СЛЕДА (никаких «вы ответили»)
 *
 * Компонент презентационный: не знает что за вопрос, не хранит состояние.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.theme.AppColors
import com.example.safespace.core.ui.theme.SafeSpaceTheme
import com.example.safespace.core.ui.theme.SafeSpaceTypography

// ── Модель действий ──────────────────────────────────────────────────────────

/** Основное действие: равноправная стеклянная кнопка («Да», «Не сегодня», «Нет»). */
data class QuestionAction(val label: String, val onClick: () -> Unit)

// ── Компонент ────────────────────────────────────────────────────────────────

@Composable
fun QuestionCard(
    question: String,
    actions: List<QuestionAction>,     // 1–3, рендерятся РАВНОПРАВНО
    modifier: Modifier = Modifier,
    quietAction: QuestionAction? = null,   // «позже» / «не сейчас»
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppColors.CardCorner))
            .background(AppColors.GlassFootnote)
            .border(
                width = 1.dp,
                color = AppColors.CardOutline,
                shape = RoundedCornerShape(AppColors.CardCorner),
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Text(
            text = "SAFESPACE",
            color = AppColors.CardCaption,
            fontSize = 10.sp,
            letterSpacing = 2.5.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = question,
            style = SafeSpaceTypography.bodyLarge,
            color = AppColors.TextPrimary,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            actions.forEach { action ->
                AnswerButton(action)
            }
            if (quietAction != null) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = quietAction.label,
                    style = SafeSpaceTypography.bodyMedium,
                    color = AppColors.CardQuietText,
                    modifier = Modifier
                        // Тихий текст, но полноценный тач-таргет:
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = quietAction.onClick)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                )
            }
        }
    }
}

/** Равноправная кнопка ответа: одинаковое стекло для ВСЕХ вариантов —
 *  никакой визуальной иерархии «правильного» ответа (Закон 1). */
@Composable
private fun AnswerButton(action: QuestionAction) {
    Text(
        text = action.label,
        style = SafeSpaceTypography.labelLarge,
        color = AppColors.CardButtonText,
        modifier = Modifier
            .clip(RoundedCornerShape(AppColors.CardButtonCorner))
            .background(AppColors.CardButtonGlass)
            .border(
                1.dp,
                AppColors.CardButtonOutline,
                RoundedCornerShape(AppColors.CardButtonCorner),
            )
            .clickable(onClick = action.onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
    )
}

// ── Обёртка исчезновения «без следа» ────────────────────────────────────────

/**
 * Уход карточки: тихое схлопывание, без конфетти и следов.
 * visible переключает ViewModel ПОСЛЕ записи ответа (данные → состояние → UI).
 * ВАЖНО: у item'а карточки в LazyColumn — стабильный key (id вопроса),
 * иначе анимация ухода достанется соседнему элементу.
 */
@Composable
fun QuestionCardSlot(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        exit = shrinkVertically(animationSpec = tween(220)) + fadeOut(tween(180)),
    ) {
        content()
    }
}

// ── Витрина / Preview ───────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun QuestionCardPreview() {
    SafeSpaceTheme() {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // F-11 «Состоялось?»
            QuestionCard(
                question = "Тренировка была?",
                actions = listOf(
                    QuestionAction("Да") {},
                    QuestionAction("Не сегодня") {},
                ),
                quietAction = QuestionAction("позже") {},
            )

            // F-06 Детект подписки
            QuestionCard(
                question = "Кажется, Netflix $12 — это подписка. Верно?",
                actions = listOf(
                    QuestionAction("Да") {},
                    QuestionAction("Нет") {},
                ),
            )

            // F-15 Инсайты
            QuestionCard(
                question = "Заметил кое-что за неделю. Взглянуть? 👀",
                actions = listOf(
                    QuestionAction("Взглянуть") {},
                ),
                quietAction = QuestionAction("не сейчас") {},
            )
        }
    }
}
