package com.offgrid.note.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.offgrid.note.data.model.Note
import com.offgrid.note.ui.theme.LocalOffgridTheme

@Composable
fun EditDialog(
    note: Note,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    onFinalConfirm: () -> Unit
) {
    val theme = LocalOffgridTheme.current
    var editText by remember { mutableStateOf(note.content) }
    var showFinalConfirm by remember { mutableStateOf(false) }
    
    val isSecondEdit = note.editCount == 2
    val isReadOnly = note.isReadOnly

    AnimatedVisibility(visible = showFinalConfirm) {
        Dialog(
            onDismissRequest = { showFinalConfirm = false },
            properties = DialogProperties()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, theme.border),
                color = theme.background,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "最终思绪确认",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "此次修改后，该笔记将定格为只读记忆，无法撤回。",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = theme.text.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showFinalConfirm = false }) {
                            Text(
                                "返回",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = theme.text
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (editText.isNotBlank()) {
                                    onSave(editText.trim())
                                    onFinalConfirm()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.border,
                                contentColor = if (theme.isDark) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                "写入定格",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, theme.border),
            color = theme.background,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isReadOnly) "查看笔记" else "编辑笔记",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )
                    if (isSecondEdit) {
                        Text(
                            text = "最后一次修改",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = theme.border
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .background(theme.background)
                        .border(1.dp, theme.border.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = editText,
                        onValueChange = { newValue -> if (!isReadOnly) editText = newValue },
                        enabled = !isReadOnly,
                        textStyle = TextStyle(
                            color = if (isReadOnly) theme.text.copy(alpha = 0.4f) else theme.text,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(theme.border),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                AnimatedVisibility(visible = isSecondEdit) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "慎重，这是你最后一次修改机会",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = theme.border
                        )
                    }
                }
                
                AnimatedVisibility(visible = isReadOnly) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "此笔记已定格为只读记忆",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = theme.text.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancel) {
                        Text(
                            text = "取消",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = theme.text
                        )
                    }
                    
                    if (!isReadOnly) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (note.editCount == 2) {
                                    showFinalConfirm = true
                                } else {
                                    if (editText.isNotBlank()) {
                                        onSave(editText.trim())
                                    }
                                }
                            },
                            enabled = editText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.border,
                                contentColor = if (theme.isDark) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                text = if (note.editCount == 2) "写入定格" else "保存",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
