package com.example.safespace.core.ui.components.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TimelineRow(
    modifier: Modifier = Modifier,
    gutterWidth: Dp = 40.dp,
    gutter: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.heightIn(min = 44.dp)
    ) {
        Box(
            Modifier.width(gutterWidth)
        ) {
            gutter()
        }
        Spacer(Modifier.width(10.dp))

        content()
    }
}