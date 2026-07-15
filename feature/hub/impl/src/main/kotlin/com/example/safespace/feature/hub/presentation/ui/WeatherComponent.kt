package com.example.safespace.feature.hub.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.ui.components.emoji
import com.example.safespace.core.ui.theme.AppColors
import kotlin.random.Random

/**
 * Иконка погоды в шапке дня.
 *
 * Рендерит одно из трёх состояний в зависимости от комбинации параметров:
 * - [weather] != null — заполненная иконка (40dp, opacity 75%)
 * - [weather] == null && [isToday] == true — пустое dashed-облако (приглашение, не поле)
 * - [weather] == null && [isToday] == false — ничего (пропущенный день не акцентируется)
 *
 * @param weather текущая погода дня; null если не выставлена
 * @param isToday true для сегодняшнего дня, false для прошлых
 * @param onClick вызывается при касании — открыть [WeatherFan] для выбора
 */
@Composable
fun DayHeaderWeatherIcon(
    weather: WeatherIcon?,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        weather != null -> Box(
            modifier = modifier
                .size(AppColors.HeaderIcon)
                .clip(CircleShape)
                .background(AppColors.HeaderIconBg)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = weather.emoji,
                fontSize = 22.sp,
                modifier = Modifier.alpha(AppColors.IconAlpha),
            )
        }

        isToday -> GhostCloud(onClick = onClick, modifier = modifier)

        else -> Unit
    }
}

@Composable
private fun GhostCloud(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(AppColors.HeaderIcon)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(AppColors.HeaderIcon)) {
            drawCircle(
                color = AppColors.GhostStroke,
                radius = size.minDimension / 2f - 1.dp.toPx(),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect
                        .dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
                ),
            )
        }
        Text("☁", fontSize = 16.sp, modifier = Modifier.alpha(0.45f))
    }
}

/**
 * Горизонтальный ряд из пяти иконок погоды для выбора значения.
 *
 * Появляется/скрывается через [AnimatedVisibility] с анимацией раскрытия влево (~180мс).
 * Состояние [expanded] держит вызывающая сторона.
 *
 * @param expanded true — ряд видим
 * @param selected текущий выбор; null если погода не выставлена
 * @param onPick вызывается при выборе иконки; вызывающая сторона должна закрыть веер
 */
@Composable
fun WeatherFan(
    expanded: Boolean,
    selected: WeatherIcon?,
    onPick: (WeatherIcon) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier,
        enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.8f) +
                expandHorizontally(tween(180), expandFrom = Alignment.End),
        exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.8f) +
                shrinkHorizontally(tween(150), shrinkTowards = Alignment.End),
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AppColors.FanGlass)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIcon.entries.forEach { w ->
                FanIcon(weather = w, isSelected = w == selected, onClick = { onPick(w) })
            }
        }
    }
}

@Composable
private fun FanIcon(weather: WeatherIcon, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(AppColors.TapTarget)
            .clip(CircleShape)
            .background(if (isSelected) AppColors.FanSelected else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = weather.emoji,
            fontSize = AppColors.IconGlyph,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.9f),
        )
    }
}

/**
 * Canvas-оверлей с атмосферным эффектом текущей погоды. Размещается поверх контента
 * секции дня через `Box + Modifier.matchParentSize()`, касания не перехватывает.
 *
 * Активные эффекты: [WeatherIcon.RAIN] и [WeatherIcon.STORM] — анимированные капли,
 * [WeatherIcon.SUNNY] — статичные блики верхних граней. Остальные состояния — без эффекта.
 *
 * Жизненный цикл эффекта привязан к композиции: при выходе composable из LazyColumn
 * [LaunchedEffect] отменяется автоматически.
 */
@Composable
fun LiveGlassOverlay(
    weather: WeatherIcon?,
    modifier: Modifier = Modifier,
) {
    when (weather) {
        WeatherIcon.RAIN, WeatherIcon.STORM -> RainOverlay(heavy = weather == WeatherIcon.STORM, modifier = modifier)
        WeatherIcon.SUNNY -> SunGlintOverlay(modifier = modifier)
        else -> Unit
    }
}

/**
 * Мутабельные поля — намеренно: объекты переиспользуются в пуле вместо аллокации новых
 * при сбросе капли за нижний край.
 */
private class Drop(var x: Float, var y: Float, var speed: Float, var len: Float)

@Composable
private fun RainOverlay(heavy: Boolean, modifier: Modifier = Modifier) {
    val count = if (heavy) 26 else 14
    val drops = remember { mutableStateListOf<Drop>() }
    var tick by remember { mutableStateOf(0L) }

    LaunchedEffect(heavy) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) {
                    val dt = (now - last) / 1_000_000f
                    drops.forEach { d ->
                        d.y += d.speed * dt
                        if (d.y > 1f) { d.y = -0.05f; d.x = Random.nextFloat() }
                    }
                }
                last = now
                tick = now
            }
        }
    }

    Canvas(modifier = modifier) {
        if (drops.isEmpty()) {
            repeat(count) {
                drops.add(
                    Drop(
                        x = Random.nextFloat(),
                        y = Random.nextFloat(),
                        speed = 0.0006f + Random.nextFloat() * 0.0009f,
                        len = 6f + Random.nextFloat() * 10f,
                    )
                )
            }
        }
        @Suppress("UNUSED_EXPRESSION") tick // чтение tick регистрирует snapshot-подписку → перерисовка каждый кадр
        drops.forEach { d ->
            val px = d.x * size.width
            val py = d.y * size.height
            drawLine(
                color = AppColors.RainDrop,
                start = Offset(px, py),
                end = Offset(px, py + d.len),
                strokeWidth = 1.2f,
            )
        }
    }
}

// Максимум 16% alpha — солнечные блики визуально подчинены янтарю подтверждённого намерения (~78%)
@Composable
private fun SunGlintOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sun")
    val breath by transition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse),
        label = "glint",
    )
    Canvas(modifier = modifier) {
        drawLine(
            color = AppColors.SunGlint.copy(alpha = breath),
            start = Offset(size.width * 0.1f, 2f),
            end = Offset(size.width * 0.55f, 2f),
            strokeWidth = 1.5f,
        )
        drawLine(
            color = AppColors.SunGlint.copy(alpha = breath * 0.7f),
            start = Offset(size.width * 0.12f, 10f),
            end = Offset(size.width * 0.4f, 10f),
            strokeWidth = 1f,
        )
    }
}

@Preview(name = "Header — ghost (today, no weather)", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewHeaderGhost() {
    DayHeaderWeatherIcon(weather = null, isToday = true, onClick = {})
}

@Preview(name = "Header — filled", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewHeaderFilled() {
    DayHeaderWeatherIcon(weather = WeatherIcon.PARTLY, isToday = true, onClick = {})
}

@Preview(name = "Header — past day, no weather", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewHeaderPastEmpty() {
    DayHeaderWeatherIcon(weather = null, isToday = false, onClick = {})
}

@Preview(name = "WeatherFan — expanded, SUNNY selected", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewFanExpanded() {
    WeatherFan(expanded = true, selected = WeatherIcon.SUNNY, onPick = {})
}

@Preview(name = "LiveGlassOverlay — RAIN", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewOverlayRain() {
    Box(Modifier.width(320.dp).height(160.dp)) {
        LiveGlassOverlay(weather = WeatherIcon.RAIN, modifier = Modifier.fillMaxSize())
    }
}

@Preview(name = "LiveGlassOverlay — STORM", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewOverlayStorm() {
    Box(Modifier.width(320.dp).height(160.dp)) {
        LiveGlassOverlay(weather = WeatherIcon.STORM, modifier = Modifier.fillMaxSize())
    }
}

@Preview(name = "LiveGlassOverlay — SUNNY", showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun PreviewOverlaySun() {
    Box(Modifier.width(320.dp).height(160.dp)) {
        LiveGlassOverlay(weather = WeatherIcon.SUNNY, modifier = Modifier.fillMaxSize())
    }
}
