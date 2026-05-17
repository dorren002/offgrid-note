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
                        text = "Final Thought Confirmation",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "After this modification, the note will be finalized as read-only and cannot be undone.",
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
                                "Back",
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
                                "Finalize",
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
                        text = when {
                            isReadOnly -> "View Note"
                            else -> "Edit Note"
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )
                    if (isSecondEdit) {
                        Text(
                            text = "Final Edit",
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
                            text = "Caution, this is your last chance to edit",
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
                            text = "This note is now locked as read-only memory",
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
                            text = "Cancel",
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
                                text = if (note.editCount == 2) "Finalize" else "Save",
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
