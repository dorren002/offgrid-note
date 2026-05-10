package com.offgrid.note.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.components.DesktopNoteCard
import com.offgrid.note.ui.components.EditDialog
import com.offgrid.note.ui.components.SettingsDialog
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class YearGroup(val year: Int, val notes: List<Note>)

@Composable
fun OffgridDesktopApp(repository: NoteRepository) {
    var notes by remember { mutableStateOf(repository.getAllNotes()) }
    var inputText by remember { mutableStateOf("") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedContent by remember { mutableStateOf("") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(OffgridThemeData.Default) }
    
    val yearGroups = remember(notes) {
        notes.groupBy { it.createdAt.toLocalDateTime(TimeZone.UTC).year }
            .map { YearGroup(it.key, it.value) }
            .sortedByDescending { it.year }
    }
    
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorVisible = true
            kotlinx.coroutines.delay(530)
            cursorVisible = false
            kotlinx.coroutines.delay(400)
        }
    }
    
    val filteredNotes = selectedYear?.let { year ->
        notes.filter { it.createdAt.toLocalDateTime(TimeZone.UTC).year == year }
    } ?: notes

    CompositionLocalProvider(LocalOffgridTheme provides currentTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentTheme.background)
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                        .background(currentTheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "offgrid.core",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = currentTheme.text.copy(alpha = 0.5f)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(currentTheme.border.copy(alpha = 0.2f))
                    )
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        yearGroups.forEach { yearGroup ->
                            val isSelected = selectedYear == yearGroup.year
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .clickable {
                                        selectedYear = if (isSelected) null else yearGroup.year
                                    }
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ">",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = if (isSelected) currentTheme.border else currentTheme.text.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${yearGroup.year}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = if (isSelected) currentTheme.text else currentTheme.text.copy(alpha = 0.7f)
                                    )
                                }
                                Text(
                                    text = "(${yearGroup.notes.size} notes)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = currentTheme.text.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(currentTheme.border.copy(alpha = 0.2f))
                    )
                    
                    Button(
                        onClick = {
                            exportedContent = repository.exportToMarkdown()
                            showExportDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.border,
                            contentColor = if (currentTheme.isDark) Color.Black else Color.White
                        )
                    ) {
                        Text(
                            text = "[ EXPORT MD ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(currentTheme.border.copy(alpha = 0.2f))
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(currentTheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "offgrid TERMINAL_",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.border
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "offgrid.core",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = currentTheme.text.copy(alpha = 0.5f),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Local encryption locked",
                                tint = currentTheme.border,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { showSettingsDialog = true },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = currentTheme.text.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(currentTheme.border.copy(alpha = 0.3f))
                    )
                    
                    LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredNotes,
                            key = { note: Note -> note.id }
                        ) { note: Note ->
                            DesktopNoteCard(
                                note = note,
                                isEditing = editingNoteId == note.id,
                                onEdit = {
                                    selectedNote = note
                                    editingNoteId = note.id
                                    showEditDialog = true
                                },
                                modifier = Modifier.widthIn(max = 320.dp)
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(currentTheme.border.copy(alpha = 0.3f))
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .background(currentTheme.background)
                                .border(1.dp, currentTheme.border),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "secure input",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = currentTheme.text.copy(alpha = 0.5f)
                                    )
                                    AnimatedVisibility(visible = cursorVisible) {
                                        Text(
                                            text = "_",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            color = currentTheme.border
                                        )
                                    }
                                }
                            }
                            BasicTextField(
                                value = inputText,
                                onValueChange = { newValue -> inputText = newValue },
                                textStyle = TextStyle(
                                    color = currentTheme.text,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                ),
                                cursorBrush = SolidColor(currentTheme.border),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp),
                                singleLine = true
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    repository.insertNote(inputText.trim())
                                    notes = repository.getAllNotes()
                                    inputText = ""
                                }
                            },
                            enabled = inputText.isNotBlank(),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = currentTheme.border,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        val currentNote = selectedNote
        
        AnimatedVisibility(visible = showEditDialog && currentNote != null) {
            if (currentNote != null) {
                EditDialog(
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
        }

        AnimatedVisibility(visible = showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                containerColor = currentTheme.background,
                titleContentColor = currentTheme.text,
                textContentColor = currentTheme.text,
                modifier = Modifier.border(1.dp, currentTheme.border),
                title = {
                    Text(
                        "Export Markdown",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(currentTheme.background)
                            .border(1.dp, currentTheme.border.copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = exportedContent,
                            onValueChange = {},
                            enabled = false,
                            textStyle = TextStyle(
                                color = currentTheme.text.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            java.awt.Toolkit.getDefaultToolkit().systemClipboard.setContents(
                                java.awt.datatransfer.StringSelection(exportedContent),
                                null
                            )
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.border,
                            contentColor = if (currentTheme.isDark) Color.Black else Color.White
                        )
                    ) {
                        Text(
                            "Copy to Clipboard",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showExportDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.border.copy(alpha = 0.2f),
                            contentColor = currentTheme.text
                        )
                    ) {
                        Text(
                            "Close",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentTheme = currentTheme,
                onThemeChange = { newTheme -> currentTheme = newTheme },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}
