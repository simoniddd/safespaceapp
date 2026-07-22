package com.example.safespace.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val TitleFont = FontFamily.Serif
val BodyFont = FontFamily.SansSerif
val MonoFont = FontFamily.Monospace

 val Beige = Color(0xFFDCCDBA)
 val Ink = Color(0xFF141414)
 val AccentYel = Color(0xFFF6EA00)
 val AccentRed = Color(0xFFFF5E5E)
 val SheetBg = Color(0xFFDCD0C2).copy(alpha = 0.92f) // полупрозрачный «стеклянный» фон шита

val SafeSpaceTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

// Моноцифры для Health-жанра: числа не «прыгают» при смене значений (tabular numerals).
// FontFamily.Monospace даёт tabular по умолчанию, "tnum" — страховка для sans-шрифтов в будущем.
val BodyDigitStyle: TextStyle = TextStyle(
    fontFamily = MonoFont,
    fontFeatureSettings = "tnum",
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 20.sp,
)

// Капитель для «печати источника»: короткие 2–4 буквы, широкий трекинг.
val SourceStampStyle: TextStyle = TextStyle(
    fontFamily = BodyFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 9.sp,
    lineHeight = 10.sp,
    letterSpacing = 1.2.sp,
)
