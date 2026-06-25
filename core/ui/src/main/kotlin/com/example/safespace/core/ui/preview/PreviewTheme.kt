package com.example.safespace.core.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.safespace.core.ui.theme.SafeSpaceTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreview

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    SafeSpaceTheme { content() }
}
