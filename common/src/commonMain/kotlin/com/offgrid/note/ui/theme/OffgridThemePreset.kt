package com.offgrid.note.ui.theme

import androidx.compose.ui.graphics.Color

data class OffgridThemePreset(
    val name: String,
    val background: Color,
    val border: Color,
    val text: Color,
    val isDark: Boolean = true
)

object OffgridThemePresets {
    val presets = listOf(
        OffgridThemePreset(
            name = "Phosphor Green (Default)",
            background = Color(0xFF000000),
            border = Color(0xFF00FF88),
            text = Color(0xFFE0E0E0)
        ),
        OffgridThemePreset(
            name = "Matrix",
            background = Color(0xFF001100),
            border = Color(0xFF00FF00),
            text = Color(0xFF00CC00)
        ),
        OffgridThemePreset(
            name = "Night Purple",
            background = Color(0xFF0D0014),
            border = Color(0xFF9B59B6),
            text = Color(0xFFE0E0E0)
        ),
        OffgridThemePreset(
            name = "Geek Blue",
            background = Color(0xFF000510),
            border = Color(0xFF3498DB),
            text = Color(0xFFE0E0E0)
        ),
        OffgridThemePreset(
            name = "Flame Red",
            background = Color(0xFF0D0000),
            border = Color(0xFFE74C3C),
            text = Color(0xFFE0E0E0)
        ),
        OffgridThemePreset(
            name = "Amber Orange",
            background = Color(0xFF0D0800),
            border = Color(0xFFE67E22),
            text = Color(0xFFE0E0E0)
        ),
        OffgridThemePreset(
            name = "Minimal White",
            background = Color(0xFFFAFAFA),
            border = Color(0xFF2C3E50),
            text = Color(0xFF2C3E50),
            isDark = false
        ),
        OffgridThemePreset(
            name = "Vintage Paper",
            background = Color(0xFFF5E6D3),
            border = Color(0xFF8B4513),
            text = Color(0xFF3E2723),
            isDark = false
        )
    )
}
