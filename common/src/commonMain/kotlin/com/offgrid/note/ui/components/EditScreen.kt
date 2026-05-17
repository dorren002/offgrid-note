package com.offgrid.note.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.ui.theme.LocalOffgridTheme

@Composable
fun EditScreen(
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
            Row(
                modifier = Modifier
                    .clickable { onCancel() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = theme.text,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = theme.text
                )
            }

            Text(
                text = when {
                    isReadOnly -> "View Note"
                    isSecondEdit -> "Final Edit"
                    else -> "Edit Note"
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                color = if (isSecondEdit) theme.border else theme.text
            )

            if (!isReadOnly) {
                Text(
                    text = if (note.editCount == 2) "Finalize" else "Save",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = if (editText.isNotBlank()) theme.border else theme.border.copy(alpha = 0.3f),
                    modifier = Modifier
                        .clickable(enabled = editText.isNotBlank()) {
                            if (note.editCount == 2) {
                                showFinalConfirm = true
                            } else {
                                if (editText.isNotBlank()) {
                                    onSave(editText.trim())
                                }
                            }
                        }
                        .padding(12.dp, 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(theme.border.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = editText,
                onValueChange = { newValue -> if (!isReadOnly) editText = newValue },
                enabled = !isReadOnly,
                textStyle = TextStyle(
                    color = if (isReadOnly) theme.text.copy(alpha = 0.4f) else theme.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(theme.border),
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(visible = isSecondEdit && !isReadOnly) {
            Column(
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Caution, this is your last chance to edit",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = theme.border
                )
            }
        }

        AnimatedVisibility(visible = isReadOnly) {
            Column(
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "This note is now locked as read-only memory",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = theme.text.copy(alpha = 0.5f)
                )
            }
        }
    }

    AnimatedVisibility(visible = showFinalConfirm) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { showFinalConfirm = false },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(theme.background)
                    .border(1.dp, theme.border)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Final Thought Confirmation",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        color = theme.text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "After this modification, the note will be finalized as read-only and cannot be undone.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = theme.text.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "Back",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = theme.text,
                            modifier = Modifier
                                .clickable { showFinalConfirm = false }
                                .padding(12.dp, 8.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "Finalize",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = if (editText.isNotBlank()) theme.border else theme.border.copy(alpha = 0.3f),
                            modifier = Modifier
                                .clickable(enabled = editText.isNotBlank()) {
                                    if (editText.isNotBlank()) {
                                        onSave(editText.trim())
                                        onFinalConfirm()
                                    }
                                }
                                .padding(12.dp, 8.dp)
                        )
                    }
                }
            }
        }
    }
}
