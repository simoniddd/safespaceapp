package com.example.safespace.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.safespace.core.ui.R

object Fonts {
    private val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    val DmSerifDisplay: FontFamily = FontFamily(
        Font(googleFont = GoogleFont("DM Serif Display"), fontProvider = provider)
    )

    val SpaceMono: FontFamily = FontFamily(
        Font(googleFont = GoogleFont("Space Mono"), fontProvider = provider)
    )
}