package com.example.safespace.core.ui.theme

import androidx.compose.ui.graphics.Color

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
}
