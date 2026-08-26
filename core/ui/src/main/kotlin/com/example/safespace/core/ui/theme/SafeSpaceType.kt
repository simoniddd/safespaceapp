package com.example.safespace.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.accentYel
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.onSurfaceCaption
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.onSurfacePrimary
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.onSurfaceSecondary
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.onSurfaceStamp
import com.example.safespace.core.ui.theme.SafeSpaceTokens.Palette.onSurfaceTertiary

object SafeSpaceType {
    // Антиква — обложка дня + суммы
    val dayHeaderPresent: TextStyle = TextStyle(
        fontFamily = Fonts.DmSerifDisplay,
        fontSize = 28.sp,
        color = onSurfacePrimary
    )
    val dayHeaderMuted: TextStyle = TextStyle(
        fontFamily = Fonts.DmSerifDisplay,
        fontSize = 26.sp,
        color = Color.White.copy(alpha = 0.6f)
    )
    val amountExpense: TextStyle = TextStyle(
        fontFamily = Fonts.DmSerifDisplay,
        fontSize = 15.sp,
        color = accentYel
    )
    val amountLinked: TextStyle = TextStyle(
        fontFamily = Fonts.DmSerifDisplay,
        fontSize = 12.sp,
        color = accentYel.copy(alpha = 0.65f)
    )

    // Моно — капители, время, tnum
    val caplineDate: TextStyle = TextStyle(
        fontFamily = Fonts.SpaceMono,
        fontSize = 9.sp,
        letterSpacing = 2.sp,
        color = onSurfaceCaption
    )
    val nowLineTime: TextStyle = TextStyle(
        fontFamily = Fonts.SpaceMono,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = onSurfaceSecondary
    )
    val healthDigit: TextStyle = TextStyle(
        fontFamily = Fonts.SpaceMono,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = onSurfaceSecondary,
        fontFeatureSettings = "tnum"
    )
    val timeGutter: TextStyle = TextStyle(
        fontFamily = Fonts.SpaceMono,
        fontSize = 9.sp,
        color = onSurfaceCaption
    )
    val sourceStamp: TextStyle = TextStyle(
        fontFamily = Fonts.SpaceMono,
        fontSize = 8.sp,
        letterSpacing = 1.5.sp,
        color = onSurfaceStamp
    )

    // Sans — тело
    val expenseTitle: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = onSurfaceTertiary
    )
    val eventTitle: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = onSurfacePrimary.copy(alpha = 0.88f)
    )
    val questionBody: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = onSurfaceSecondary
    )
    val answerLabel: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = onSurfaceSecondary
    )
}