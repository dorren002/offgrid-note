package com.offgrid.note.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffgridNoteApp(repository: NoteRepository) {
    val currentTheme = OffgridThemeData.Default
    CompositionLocalProvider(LocalOffgridTheme provides currentTheme) {
        val theme = LocalOffgridTheme.current
        var notes by remember { mutableStateOf(repository.getAllNotes()) }
        var inputText by remember { mutableStateOf("") }
        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }
        var selectedNote by remember { mutableStateOf<Note?>(null) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showMenu by remember { mutableStateOf(false) }
        var menuAnchorNote by remember { mutableStateOf<Note?>(null) }

        val listState = rememberLazyListState()

        LaunchedEffect(notes) {
            if (notes.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { newValue: String ->
                                        searchQuery = newValue
                                        if (newValue.isNotEmpty()) {
                                            notes = repository.searchNotes(newValue)
                                        } else {
                                            notes = repository.getAllNotes()
                                        }
                                    },
                                    textStyle = TextStyle(color = theme.text),
                                    singleLine = true,
                                    cursorBrush = SolidColor(theme.border),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Text(
                                "Offgrid Note",
                                fontWeight = FontWeight.Bold,
                                color = theme.text
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) {
                                searchQuery = ""
                                notes = repository.getAllNotes()
                            }
                        }) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "Close search" else "Search",
                                tint = theme.text
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = theme.background
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = theme.background,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(theme.background)
                                .border(1.dp, theme.border, RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            BasicTextField(
                                value = inputText,
                                onValueChange = { newValue: String -> inputText = newValue },
                                textStyle = TextStyle(
                                    color = theme.text,
                                    fontSize = 16.sp
                                ),
                                cursorBrush = SolidColor(theme.border),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 5
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledIconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    repository.insertNote(inputText.trim())
                                    notes = repository.getAllNotes()
                                    inputText = ""
                                }
                            },
                            enabled = inputText.isNotBlank(),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = theme.border,
                                contentColor = if (theme.isDark) Color.Black else Color.White
                            )
                        ) {
                            Text("↑", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(theme.background)
            ) {
                if (notes.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No notes yet",
                            style = MaterialTheme.typography.titleLarge,
                            color = theme.text.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start capturing your thoughts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = theme.text.copy(alpha = 0.3f)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onLongClick = {
                                    menuAnchorNote = note
                                    showMenu = true
                                },
                                onClick = {
                                    if (note.isReadOnly) {
                                        selectedNote = note
                                        showEditDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showMenu && menuAnchorNote != null) {
            AlertDialog(
                onDismissRequest = {
                    showMenu = false
                    menuAnchorNote = null
                },
                title = { Text("Note Options", color = theme.text) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                selectedNote = menuAnchorNote
                                showMenu = false
                                showEditDialog = true
                            },
                            enabled = menuAnchorNote?.isReadOnly == false
                        ) {
                            Text("Edit", color = theme.text)
                        }
                        TextButton(
                            onClick = {
                                selectedNote = menuAnchorNote
                                showMenu = false
                                showEditDialog = true
                            }
                        ) {
                            Text("Copy", color = theme.text)
                        }
                        TextButton(
                            onClick = {
                                selectedNote = menuAnchorNote
                                showMenu = false
                                showDeleteDialog = true
                            }
                        ) {
                            Text("Delete", color = theme.border)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = {
                        showMenu = false
                        menuAnchorNote = null
                    }) {
                        Text("Cancel", color = theme.text)
                    }
                },
                containerColor = theme.background
            )
        }

        if (showEditDialog && selectedNote != null) {
            var editText by remember { mutableStateOf(selectedNote!!.content) }

            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                    selectedNote = null
                },
                title = {
                    Text(
                        if (selectedNote!!.isReadOnly) "View Note" else "Edit Note",
                        color = theme.text
                    )
                },
                text = {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.background)
                                .border(1.dp, theme.border, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            BasicTextField(
                                value = editText,
                                onValueChange = { newValue: String ->
                                    if (selectedNote!!.isReadOnly.not()) editText = newValue
                                },
                                enabled = selectedNote!!.isReadOnly.not(),
                                textStyle = TextStyle(
                                    color = if (selectedNote!!.isReadOnly) theme.text.copy(alpha = 0.5f) else theme.text,
                                    fontSize = 16.sp
                                ),
                                cursorBrush = SolidColor(theme.border),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (selectedNote!!.isReadOnly) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This note is read-only (max edits reached)",
                                style = MaterialTheme.typography.bodySmall,
                                color = theme.border
                            )
                        }
                    }
                },
                confirmButton = {
                    if (selectedNote!!.isReadOnly.not()) {
                        TextButton(onClick = {
                            if (editText.isNotBlank() && selectedNote != null) {
                                repository.updateNote(selectedNote!!.id, editText.trim())
                                notes = repository.getAllNotes()
                            }
                            showEditDialog = false
                            selectedNote = null
                        }) {
                            Text("Save", color = theme.text)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEditDialog = false
                        selectedNote = null
                    }) {
                        Text("Cancel", color = theme.text)
                    }
                },
                containerColor = theme.background
            )
        }

        if (showDeleteDialog && selectedNote != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedNote = null
                },
                title = { Text("Delete Note", color = theme.text) },
                text = { Text("Are you sure you want to delete this note? This action cannot be undone.", color = theme.text.copy(alpha = 0.7f)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (selectedNote != null) {
                                repository.deleteNote(selectedNote!!.id)
                                notes = repository.getAllNotes()
                            }
                            showDeleteDialog = false
                            selectedNote = null
                        }
                    ) {
                        Text("Delete", color = theme.border)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        selectedNote = null
                    }) {
                        Text("Cancel", color = theme.text)
                    }
                },
                containerColor = theme.background
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val theme = LocalOffgridTheme.current
    val dateTime = note.createdAt.toLocalDateTime(TimeZone.UTC)
    val timeString = "${dateTime.date} ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = theme.background
        ),
        border = BorderStroke(1.dp, theme.border.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (note.isReadOnly) theme.text.copy(alpha = 0.5f) else theme.text
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.text.copy(alpha = 0.5f)
                )
                if (note.isReadOnly) {
                    Text(
                        text = "Read-only",
                        style = MaterialTheme.typography.bodySmall,
                        color = theme.border
                    )
                } else {
                    Text(
                        text = "··· ${note.remainingEdits}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (note.remainingEdits) {
                            1 -> theme.border
                            2 -> theme.border.copy(alpha = 0.7f)
                            else -> theme.text.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
}
