package com.example.safespace.feature.hub.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.ui.preview.PreviewTheme
import com.example.safespace.core.ui.theme.SafeSpaceType
import com.example.safespace.feature.hub.presentation.state.DayKind
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Шапка дня: капитель-дата сверху, крупный заголовок антиквой («Сегодня» / «Завтра»),
 * справа — слот погоды (только для TODAY, завтра не выбирается).
 *
 * TOMORROW отрисовывается заголовком на 60% прозрачности — день ещё не начал
 * писаться. Без облака-призрака: позвать к действию, которого нельзя сделать
 * заранее, значит создать ложную affordance.
 */
@Composable
fun DayHeader(
    date: LocalDate,
    kind: DayKind,
    weather: WeatherIcon?,
    onWeatherClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val caplineText = remember(date) { formatCapline(date) }
    val titleText = when (kind) {
        DayKind.TODAY -> "Сегодня"
        DayKind.TOMORROW -> "Завтра"
    }
    val titleStyle = when (kind) {
        DayKind.TODAY -> SafeSpaceType.dayHeaderPresent
        DayKind.TOMORROW -> SafeSpaceType.dayHeaderMuted
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = caplineText, style = SafeSpaceType.caplineDate)
            Spacer(Modifier.height(5.dp))
            Text(text = titleText, style = titleStyle)
        }

        // Погода только у TODAY — завтра её не выбрать.
        if (kind == DayKind.TODAY) {
            DayHeaderWeatherIcon(
                weather = weather,
                isToday = true,
                onClick = onWeatherClick,
            )
        }
    }
}

private val CAPLINE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE · d MMMM", Locale("ru"))

internal fun formatCapline(date: LocalDate): String =
    date.toJavaLocalDate().format(CAPLINE_FORMATTER).uppercase(Locale("ru"))

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun DayHeaderTodayFilled() {
    PreviewTheme {
        DayHeader(
            date = LocalDate(2026, 7, 4),
            kind = DayKind.TODAY,
            weather = WeatherIcon.PARTLY,
            onWeatherClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun DayHeaderTodayGhost() {
    PreviewTheme {
        DayHeader(
            date = LocalDate(2026, 7, 4),
            kind = DayKind.TODAY,
            weather = null,
            onWeatherClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun DayHeaderTomorrow() {
    PreviewTheme {
        DayHeader(
            date = LocalDate(2026, 7, 5),
            kind = DayKind.TOMORROW,
            weather = null,
            onWeatherClick = {},
        )
    }
}
