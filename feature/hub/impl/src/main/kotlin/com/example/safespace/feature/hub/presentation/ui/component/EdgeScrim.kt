package com.example.safespace.feature.hub.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safespace.core.ui.preview.PreviewTheme

private val Ink = Color(0xFF141414)

/**
 * Верхний скрим — лента уходит «под статусбар», плавно затемняясь к #141414.
 * 64dp по §4.7 брифа. Не перехватывает касания — скрим декоративный.
 */
@Composable
fun EdgeScrimTop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Ink.copy(alpha = 0.88f), Color.Transparent),
                ),
            ),
    )
}

/**
 * Нижний скрим — лента растворяется у края, FAB остаётся читаемым поверх.
 * 90dp по §4.7 брифа.
 */
@Composable
fun EdgeScrimBottom(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Ink.copy(alpha = 0.92f)),
                ),
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EdgeScrimTopPreview() {
    PreviewTheme {
        EdgeScrimTop()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun EdgeScrimBottomPreview() {
    PreviewTheme {
        EdgeScrimBottom()
    }
}
