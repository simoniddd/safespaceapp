package com.example.safespace.feature.calendar.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safespace.core.ui.theme.AppColors
import com.example.safespace.feature.calendar.presentation.action.CalendarSettingAction.OnBackClick
import com.example.safespace.feature.calendar.presentation.action.CalendarSettingAction.OnCalendarToggled
import com.example.safespace.feature.calendar.presentation.state.CalendarSettingUiState
import com.example.safespace.feature.calendar.presentation.state.CalendarUi
import com.example.safespace.feature.calendar.presentation.viwemodel.CalendarSettingViewmodel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalendarSettingScreen(
    viewmodel: CalendarSettingViewmodel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val uiState = state.toUiState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BackgroundCalendar)
    ) {
        PickerHeader(onBack = { viewmodel.onAction(OnBackClick) })

        when (uiState) {
            CalendarSettingUiState.Loading -> Unit

            is CalendarSettingUiState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                uiState.sections.forEach { section ->
                    item(key = "header_${section.title}") {
                        SectionCaption(section.title)
                    }
                    items(
                        count = section.calendars.size,
                        key = { section.calendars[it].id },
                    ) { index ->
                        val calendar = section.calendars[index]
                        CalendarRow(
                            calendar = calendar,
                            onClick = { viewmodel.onAction(OnCalendarToggled(calendar.id)) },
                        )
                    }
                }
                item(key = "footnote") {
                    Spacer(Modifier.height(14.dp))
                    Footnote(
                        "Снятый флажок скрывает события этого календаря " +
                                "из ленты. Изменения применяются сразу."
                    )
                }
            }

            CalendarSettingUiState.Empty -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Здесь пусто",
                    color = AppColors.TextSecondary,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun PickerHeader(
    onBack: () -> Unit
) {
    Column(Modifier.padding(start = 8.dp, top = 12.dp, end = 20.dp, bottom = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = AppColors.TextSecondary,
                )
            }
            Text(
                text = "НАСТРОЙКИ",
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
            )
        }
        Text(
            text = "Календари",
            color = AppColors.TextPrimary,
            fontSize = 30.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp),
        )
    }
}

@Composable
private fun SectionCaption(text: String) {
    Text(
        text = text.uppercase(),
        color = AppColors.TextSecondary,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 4.dp),
    )
}

@Composable
private fun CalendarRow(calendar: CalendarUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppColors.CornerRadius))
            .background(AppColors.GlassRow)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(calendar.color)
        )
        Text(
            text = calendar.name,
            color = if (calendar.isVisible) AppColors.TextPrimary
            else AppColors.TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f),
        )
        AmberCheckbox(checked = calendar.isVisible)
    }
}

@Composable
private fun AmberCheckbox(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (checked) AppColors.Amber.copy(alpha = 0.16f)
                else Color.White.copy(alpha = 0.05f)
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppColors.Amber,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun Footnote(text: String) {
    Text(
        text = text,
        color = AppColors.TextSecondary,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppColors.CornerRadius))
            .background(AppColors.GlassFootnote)
            .padding(16.dp),
    )
}


