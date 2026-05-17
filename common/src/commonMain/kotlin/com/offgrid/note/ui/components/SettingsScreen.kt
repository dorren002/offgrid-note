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
import androidx.compose.material.icons.filled.Download
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
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData
import com.offgrid.note.ui.theme.OffgridThemePresets

@Composable
fun SettingsScreen(
    currentTheme: OffgridThemeData,
    repository: NoteRepository,
    onThemeChange: (OffgridThemeData) -> Unit,
    onExportNotes: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempTheme by remember { mutableStateOf(currentTheme) }
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    val theme = tempTheme

    when (showColorPicker) {
        "background" -> ColorPickerScreen(
            title = "Select Background Color",
            currentColor = tempTheme.background,
            onColorSelected = { color ->
                tempTheme = tempTheme.copy(background = color)
            },
            onDismiss = { showColorPicker = null }
        )
        "border" -> ColorPickerScreen(
            title = "Select Border Color",
            currentColor = tempTheme.border,
            onColorSelected = { color ->
                tempTheme = tempTheme.copy(border = color)
            },
            onDismiss = { showColorPicker = null }
        )
        "text" -> ColorPickerScreen(
            title = "Select Text Color",
            currentColor = tempTheme.text,
            onColorSelected = { color ->
                tempTheme = tempTheme.copy(text = color)
            },
            onDismiss = { showColorPicker = null }
        )
        else -> {
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
                        text = "Settings",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )

                    TextButton(onClick = {
                        onThemeChange(tempTheme)
                        onDismiss()
                    }) {
                        Text(
                            text = "Done",
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, theme.border)
                            .clickable {
                                val markdown = repository.exportToMarkdown()
                                onExportNotes(markdown)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export",
                            tint = theme.border,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Export All Notes",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = theme.text
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(theme.border.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Preset Themes",
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
                                theme = theme,
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
                        text = "Custom Colors",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = theme.border
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorPreviewRow(
                        label = "Background",
                        color = tempTheme.background,
                        theme = theme,
                        onClick = { showColorPicker = "background" }
                    )

                    ColorPreviewRow(
                        label = "Border",
                        color = tempTheme.border,
                        theme = theme,
                        onClick = { showColorPicker = "border" }
                    )

                    ColorPreviewRow(
                        label = "Text",
                        color = tempTheme.text,
                        theme = theme,
                        onClick = { showColorPicker = "text" }
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetItem(
    preset: com.offgrid.note.ui.theme.OffgridThemePreset,
    isSelected: Boolean,
    theme: OffgridThemeData,
    onSelect: () -> Unit
) {
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

@Composable
fun ColorPreviewRow(
    label: String,
    color: Color,
    theme: OffgridThemeData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = theme.text,
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Box(
            modifier = Modifier
                .size(32.dp, 24.dp)
                .background(color)
                .border(1.dp, theme.border.copy(alpha = 0.3f))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = String.format("#%06X", 0xFFFFFF and color.hashCode()),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = theme.text.copy(alpha = 0.6f)
        )
    }
}
