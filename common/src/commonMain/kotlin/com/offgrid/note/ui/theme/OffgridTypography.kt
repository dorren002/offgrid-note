package com.offgrid.note.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MonoFont = FontFamily.Monospace

val OffgridTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        fontStyle = FontStyle.Normal
    ),
    displayMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        fontStyle = FontStyle.Normal
    ),
    displaySmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontStyle = FontStyle.Normal
    ),
    headlineLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        fontStyle = FontStyle.Normal
    ),
    headlineMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        fontStyle = FontStyle.Normal
    ),
    headlineSmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        fontStyle = FontStyle.Normal
    ),
    titleLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        fontStyle = FontStyle.Normal
    ),
    titleMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontStyle = FontStyle.Normal
    ),
    titleSmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal
    ),
    bodyLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontStyle = FontStyle.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        fontStyle = FontStyle.Normal
    ),
    bodySmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        fontStyle = FontStyle.Normal
    ),
    labelMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal
    ),
    labelSmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        fontStyle = FontStyle.Normal
    )
)