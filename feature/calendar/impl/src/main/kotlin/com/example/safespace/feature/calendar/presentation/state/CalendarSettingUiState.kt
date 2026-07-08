package com.example.safespace.feature.calendar.presentation.state

import androidx.compose.ui.graphics.Color

/**
 * UI-стейт экрана выбора календарей.
 *
 * Отделён от [CalendarSettingState] (ViewModel-стейт с сырыми данными) —
 * экран знает только про этот тип и не зависит от доменных моделей.
 *
 * [Loading] — ContentResolver ещё не вернул список; экран не рисует ничего
 * (не путать с «нет календарей» — это разные ситуации).
 * [Content] — список получен, можно рендерить.
 */
sealed interface CalendarSettingUiState {
    data object Loading : CalendarSettingUiState
    data object Empty : CalendarSettingUiState
    data class Content(val sections: List<CalendarSection>) : CalendarSettingUiState
}

/**
 * Группа календарей с заголовком в списке.
 *
 * Сейчас всегда одна секция «Мои Календари».
 * В будущем можно группировать по аккаунту Google — тогда [title] будет
 * email аккаунта, а секций станет несколько. Для этого нужно добавить
 * поле accountName в [com.example.safespace.core.domain.model.CalendarInfo].
 */
data class CalendarSection(
    val title: String,
    val calendars: List<CalendarUi>
)

/**
 * UI-представление одного календаря в строке списка.
 *
 * Это трансформация доменной [com.example.safespace.core.domain.model.CalendarInfo]:
 * - [id]: Long без конвертации — Long валиден как ключ LazyColumn и saveable в Bundle
 * - [color]: Int (из CalendarContract, ARGB) → Compose [Color]
 * - [isVisible]: derived — вычисляется как id !in hiddenCalendarIds, не хранится в БД
 */
data class CalendarUi(
    val id: Long,
    val name: String,
    val color: Color,
    val isVisible: Boolean,
)