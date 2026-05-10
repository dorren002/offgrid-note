package com.offgrid.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData
import com.offgrid.note.ui.theme.OffgridThemePresets

@Composable
fun SettingsScreen(
    currentTheme: OffgridThemeData,
    onThemeChange: (OffgridThemeData) -> Unit,
    onDismiss: () -> Unit
) {
    var tempTheme by remember { mutableStateOf(currentTheme) }
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    CompositionLocalProvider(LocalOffgridTheme provides tempTheme) {
        val theme = LocalOffgridTheme.current

        when (showColorPicker) {
            "background" -> ColorPickerDialog(
                title = "选择背景色",
                currentColor = tempTheme.background,
                onColorSelected = { color ->
                    tempTheme = tempTheme.copy(background = color)
                },
                onDismiss = { showColorPicker = null }
            )
            "border" -> ColorPickerDialog(
                title = "选择边框色",
                currentColor = tempTheme.border,
                onColorSelected = { color ->
                    tempTheme = tempTheme.copy(border = color)
                },
                onDismiss = { showColorPicker = null }
            )
            "text" -> ColorPickerDialog(
                title = "选择字体色",
                currentColor = tempTheme.text,
                onColorSelected = { color ->
                    tempTheme = tempTheme.copy(text = color)
                },
                onDismiss = { showColorPicker = null }
            )
            else -> {}
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onThemeChange(tempTheme)
                        onDismiss()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.text,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "主题设置",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = theme.text
                )

                TextButton(onClick = {
                    onThemeChange(tempTheme)
                    onDismiss()
                }) {
                    Text(
                        text = "完成",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = theme.border
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(theme.border.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "预设主题",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = theme.border
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OffgridThemePresets.presets) { preset ->
                        PresetItem(
                            preset = preset,
                            isSelected = tempTheme == OffgridThemeData.fromPreset(preset),
                            onSelect = {
                                tempTheme = OffgridThemeData.fromPreset(preset)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(theme.border.copy(alpha = 0.3f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "自定义颜色",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = theme.border
                )

                Spacer(modifier = Modifier.height(8.dp))

                ColorPreviewRow(
                    label = "背景",
                    color = tempTheme.background,
                    onClick = { showColorPicker = "background" }
                )

                ColorPreviewRow(
                    label = "边框",
                    color = tempTheme.border,
                    onClick = { showColorPicker = "border" }
                )

                ColorPreviewRow(
                    label = "字体",
                    color = tempTheme.text,
                    onClick = { showColorPicker = "text" }
                )
            }
        }
    }
}

@Composable
private fun PresetItem(
    preset: com.offgrid.note.ui.theme.OffgridThemePreset,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val theme = LocalOffgridTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .background(
                if (isSelected) theme.border.copy(alpha = 0.1f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) theme.border else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 24.dp)
                .background(preset.background)
                .border(1.dp, preset.border)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = preset.name,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = if (isSelected) theme.text else theme.text.copy(alpha = 0.7f)
        )
    }
}
