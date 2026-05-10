package com.offgrid.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.offgrid.note.ui.theme.LocalOffgridTheme

data class ColorOption(
    val name: String,
    val color: Color
)

object ColorPalette {
    val colors = listOf(
        Color(0xFF000000), Color(0xFFFFFFFF),
        Color(0xFF1A1A1A), Color(0xFF333333),
        Color(0xFF00FF88), Color(0xFF00CC66),
        Color(0xFF00FF00), Color(0xFF32CD32),
        Color(0xFF3498DB), Color(0xFF2980B9),
        Color(0xFF9B59B6), Color(0xFF8E44AD),
        Color(0xFFE74C3C), Color(0xFFC0392B),
        Color(0xFFE67E22), Color(0xFFD35400),
        Color(0xFFF1C40F), Color(0xFFF39C12),
        Color(0xFF1ABC9C), Color(0xFF16A085),
        Color(0xFF34495E), Color(0xFF2C3E50),
        Color(0xFF95A5A6), Color(0xFF7F8C8D),
        Color(0xFFBDC3C7), Color(0xFFECF0F1),
        Color(0xFF8B4513), Color(0xFFA0522D),
        Color(0xFFFF6B6B), Color(0xFFFF4757),
        Color(0xFF00D2D3), Color(0xFF01CBCB)
    )
}

@Composable
fun ColorPickerDialog(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalOffgridTheme.current
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, theme.border),
            color = theme.background,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = theme.text
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(ColorPalette.colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (color == currentColor) 2.dp else 1.dp,
                                    color = if (color == currentColor) theme.border else theme.text.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    onColorSelected(color)
                                    onDismiss()
                                }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "取消",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = theme.text.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPreviewRow(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val theme = LocalOffgridTheme.current
    
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
