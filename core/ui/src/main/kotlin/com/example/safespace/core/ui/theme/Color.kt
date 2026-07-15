package com.example.safespace.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Цветовая палитра приложения SafeSpace.
 * Сгруппирована согласно предоставленным референсам (ID палитры).
 */
object AppColors {

    // Палитра 16 (Фиолетово-Бирюзовая)
    val Palette16 = Palette(
        c1 = Color(0xFFCAABD5), c2 = Color(0xFF83749F), c3 = Color(0xFF6BC4CA),
        c4 = Color(0xFF04969E), c5 = Color(0xFF025C58)
    )

    // Палитра 19 (Бирюзово-Зеленая)
    val Palette19 = Palette(
        c1 = Color(0xFF51BFC8), c2 = Color(0xFF1C4F59), c3 = Color(0xFF78A133),
        c4 = Color(0xFFDADB5C), c5 = Color(0xFF1D380C)
    )

    // Палитра 17 (Мятно-Персиковая)
    val Palette17 = Palette(
        c1 = Color(0xFF84E2CA), c2 = Color(0xFFCCE7A4), c3 = Color(0xFFFBDE94),
        c4 = Color(0xFFF58A78), c5 = Color(0xFFC8ACD5)
    )

    // Палитра 15 (Сине-голубая)
    val Palette15 = Palette(
        c1 = Color(0xFF001A4A), c2 = Color(0xFF004576), c3 = Color(0xFF4D82A1),
        c4 = Color(0xFF0089BB), c5 = Color(0xFF93CADC), c6 = Color(0xFFD2E7EC)
    )

    // Палитра 20 (Оранжево-Зеленая)
    val Palette20 = Palette(
        c1 = Color(0xFF63B7D1), c2 = Color(0xFFE05B42), c3 = Color(0xFFEA8E5B),
        c4 = Color(0xFFFB8257), c5 = Color(0xFFC0CC99), c6 = Color(0xFF87D5C8)
    )

    // Палитра 21 (Пастельная)
    val Palette21 = Palette(
        c1 = Color(0xFF98CF96), c2 = Color(0xFFABC760), c3 = Color(0xFFF2CEC2),
        c4 = Color(0xFFF6CCA7), c5 = Color(0xFFE69695), c6 = Color(0xFFE8B2A8),
        c7 = Color(0xFFFCC888), c8 = Color(0xFDB182), c9 = Color(0xFFE8A08F),
        c10 = Color(0xFFC3C198), c11 = Color(0xFFDFC9B6)
    )

    data class Palette(
        val c1: Color, val c2: Color, val c3: Color, val c4: Color, val c5: Color,
        val c6: Color? = null, val c7: Color? = null, val c8: Color? = null,
        val c9: Color? = null, val c10: Color? = null, val c11: Color? = null
    )

    // Hub screen
    val SandBackground = Color(0xFFDCCDBA)
    val PureBlack = Color(0xFF141414)
    val NeonYellow = Color(0xFFF6EA00)
    val ElectricBlue = Color(0xFF3B5BFF)
    val SoftWhite = Color(0xFFEAE0D3)
    val CoralRed = Color(0xFFFF5E5E)

    val Primary = Color(0xFF6750A4)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFEADDFF)

    val Secondary = Color(0xFF625B71)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE8DEF8)

    val Background = Color(0xFFFFFBFE)
    val Surface = Color(0xFFFFFBFE)
    val OnBackground = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFF1C1B1F)

    val Error = Color(0xFFB3261E)
    val OnError = Color(0xFFFFFFFF)

    val Amber = Color(0xFFD9A441)
    val GlassRow = Color.White.copy(alpha = 0.055f)
    val GlassFootnote = Color.White.copy(alpha = 0.035f)
    val TextPrimary = Color.White.copy(alpha = 0.88f)
    val TextSecondary = Color.White.copy(alpha = 0.45f)
    val BackgroundCalendar = Color(0xFF141414)
    val CornerRadius = 14.dp


    // QuestionCard
    val CardOutline = Color.White.copy(alpha = 0.10f)
    val CardButtonGlass = Color.White.copy(alpha = 0.07f)
    val CardButtonOutline = Color.White.copy(alpha = 0.08f)
    val CardCaption = Color.White.copy(alpha = 0.35f)
    val CardButtonText = Color.White.copy(alpha = 0.75f)
    val CardQuietText = Color.White.copy(alpha = 0.38f)
    val CardCorner = 16.dp
    val CardButtonCorner = 12.dp

    val IconAlpha = 0.75f
    val GhostStroke = Color.White.copy(alpha = 0.28f)
    val FanGlass = Color.White.copy(alpha = 0.07f)
    val FanSelected = Color.White.copy(alpha = 0.12f)
    val HeaderIconBg = Color.White.copy(alpha = 0.05f)
    val TapTarget = 28.dp
    val IconGlyph = 24.sp
    val HeaderIcon = 40.dp
    val RainDrop = Color.White.copy(alpha = 0.28f)
    val SunGlint = Color(0xFFF6EA00)

}
