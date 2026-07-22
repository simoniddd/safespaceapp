package com.example.safespace.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.safespace.core.ui.theme.AppColors
import com.example.safespace.core.ui.theme.SourceStampStyle

/**
 * Круглая «печать источника» для Health-жанра: 26dp, короткий лейбл капителью.
 *
 * Семантика: этот блок пришёл из тела (пассивный источник), а не от пользователя.
 * Единый визуальный знак — вместо разноцветных точек, вместо разных иконок.
 */
@Composable
fun SourceStamp(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(AppColors.SourceStampSize)
            .background(color = AppColors.SourceStampBg, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase().take(4),
            style = SourceStampStyle,
            color = AppColors.SourceStampText,
        )
    }
}

/**
 * Маппинг Health Connect packageName → короткий лейбл для печати.
 * Fallback — первые буквы имени пакета (после последней точки).
 * Специфичные бренды добавляем по мере встречи в реальных данных.
 */
fun String.toSourceLabel(): String = when {
    contains("strava", ignoreCase = true) -> "STRAVA"
    contains("garmin", ignoreCase = true) -> "GARMIN"
    contains("fitness", ignoreCase = true) -> "FIT"
    contains("polar", ignoreCase = true) -> "POLAR"
    contains("healthdata", ignoreCase = true) -> "HC"
    contains("samsung", ignoreCase = true) -> "SAMS"
    isBlank() -> "HC"
    else -> substringAfterLast('.').take(4).uppercase()
}
