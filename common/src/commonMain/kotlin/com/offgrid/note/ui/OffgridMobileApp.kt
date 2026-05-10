package com.offgrid.note.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.components.EditScreen
import com.offgrid.note.ui.components.NoteCard
import com.offgrid.note.ui.components.SettingsScreen
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData

@Composable
fun OffgridMobileApp(repository: NoteRepository) {
    var notes by remember { mutableStateOf(repository.getAllNotes()) }
    var inputText by remember { mutableStateOf("") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(OffgridThemeData.Default) }
    
    val listState = rememberLazyListState()
    
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorVisible = true
            kotlinx.coroutines.delay(530)
            cursorVisible = false
            kotlinx.coroutines.delay(400)
        }
    }

    CompositionLocalProvider(LocalOffgridTheme provides currentTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentTheme.background)
        ) {
            val topBarHeight = 57.dp
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(top = topBarHeight, bottom = 120.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        isEditing = editingNoteId == note.id,
                        onEdit = {
                            selectedNote = note
                            editingNoteId = note.id
                            showEditDialog = true
                        }
                    )
                }
            }

            TopBar(
                theme = currentTheme,
                onSettingsClick = { showSettingsDialog = true },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            BottomArea(
                theme = currentTheme,
                inputText = inputText,
                cursorVisible = cursorVisible,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        repository.insertNote(inputText.trim())
                        notes = repository.getAllNotes()
                        inputText = ""
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            val currentNote = selectedNote
            
            if (showEditDialog && currentNote != null) {
                EditScreen(
                    note = currentNote,
                    onSave = { newContent ->
                        repository.updateNote(currentNote.id, newContent)
                        notes = repository.getAllNotes()
                        editingNoteId = null
                        showEditDialog = false
                        selectedNote = null
                    },
                    onCancel = {
                        editingNoteId = null
                        showEditDialog = false
                        selectedNote = null
                    },
                    onFinalConfirm = {
                        repository.updateNote(currentNote.id, currentNote.content)
                        notes = repository.getAllNotes()
                        editingNoteId = null
                        showEditDialog = false
                        selectedNote = null
                    }
                )
            }

            if (showSettingsDialog) {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onThemeChange = { newTheme -> currentTheme = newTheme },
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    theme: OffgridThemeData,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(theme.background)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OffGrid.Note",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = theme.text.copy(alpha = 0.5f)
            )
            Row {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Local encryption locked",
                    tint = theme.border,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = theme.text.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(theme.border.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun BottomArea(
    theme: OffgridThemeData,
    inputText: String,
    cursorVisible: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(theme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(theme.border.copy(alpha = 0.3f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .border(1.dp, theme.border)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "secure input",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = theme.text.copy(alpha = 0.5f)
                        )
                        AnimatedVisibility(visible = cursorVisible) {
                            Text(
                                text = "_",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = theme.border
                            )
                        }
                    }
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    textStyle = TextStyle(
                        color = theme.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(theme.border),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            IconButton(
                onClick = onSend,
                enabled = inputText.isNotBlank(),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = theme.border,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Think outside the cloud.",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.text.copy(alpha = 0.3f)
            )
        }
    }
}