package com.example.safespace.core.ui.theme

import androidx.compose.ui.graphics.Color

object SafeSpaceTokens {
    object Glass {
        val body = Color.White.copy(alpha = 0.045f)
        val expense = Color.White.copy(alpha = 0.055f)
        val event = Color.White.copy(alpha = 0.070f)
        val fab = Color.White.copy(alpha = 0.090f)
    }

    val AmberIntention = Color(0xFFFFAF23)

    object Palette {
        val ink = Color(0xFF141414)
        val accentYel = Color(0xFFF6EA00)
        // семантические альфы над Color.White
        val onSurfacePrimary   = Color.White.copy(alpha = 0.88f)
        val onSurfaceSecondary = Color.White.copy(alpha = 0.72f)
        val onSurfaceTertiary  = Color.White.copy(alpha = 0.55f)
        val onSurfaceFuture    = Color.White.copy(alpha = 0.42f)
        val onSurfaceCaption   = Color.White.copy(alpha = 0.28f)
        val onSurfaceStamp     = Color.White.copy(alpha = 0.22f)
    }
}