package com.offgrid.note.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class OffgridThemeData(
    val background: Color,
    val border: Color,
    val text: Color,
    val isDark: Boolean = true
) {
    companion object {
        val Default = OffgridThemeData(
            background = Color(0xFF000000),
            border = Color(0xFF00FF88),
            text = Color(0xFFE0E0E0)
        )

        fun fromPreset(preset: OffgridThemePreset): OffgridThemeData {
            return OffgridThemeData(
                background = preset.background,
                border = preset.border,
                text = preset.text,
                isDark = preset.isDark
            )
        }
    }
}

val LocalOffgridTheme = compositionLocalOf { OffgridThemeData.Default }

val PhosphorGreen = Color(0xFF00FF88)
val PhosphorGreenDim = Color(0xFF00AA55)
val BackgroundColor = Color(0xFF000000)
val SidebarColor = Color(0xFF0A0A0A)
val SurfaceColor = Color(0xFF111111)
val TextColor = Color(0xFFE0E0E0)
val TextColorDim = Color(0xFF888888)
val BorderColor = Color(0xFF222222)
val RedHighlight = Color(0xFFFF4444)

@Composable
fun OffgridTheme(
    offgridTheme: OffgridThemeData = OffgridThemeData.Default,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = offgridTheme.border,
        background = offgridTheme.background,
        surface = SurfaceColor,
        onPrimary = if (offgridTheme.isDark) Color.Black else Color.White,
        onBackground = offgridTheme.text,
        onSurface = offgridTheme.text,
        error = RedHighlight
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OffgridTypography,
        content = content
    )
}
