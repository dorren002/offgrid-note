package com.offgrid.note.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffgridNoteApp(repository: NoteRepository) {
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
                                textStyle = TextStyle(color = Color.White),
                                singleLine = true,
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Text(
                            "Offgrid Note",
                            fontWeight = FontWeight.Bold
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
                            contentDescription = if (isSearchActive) "Close search" else "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1A1A1A),
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
                            .background(Color(0xFF2A2A2A))
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { newValue: String -> inputText = newValue },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
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
                            containerColor = MaterialTheme.colorScheme.primary
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
                .background(Color.Black)
        ) {
            if (notes.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start capturing your thoughts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray.copy(alpha = 0.7f)
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
            title = { Text("Note Options") },
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
                        Text("Edit")
                    }
                    TextButton(
                        onClick = {
                            selectedNote = menuAnchorNote
                            showMenu = false
                            showEditDialog = true
                        }
                    ) {
                        Text("Copy")
                    }
                    TextButton(
                        onClick = {
                            selectedNote = menuAnchorNote
                            showMenu = false
                            showDeleteDialog = true
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showMenu = false
                    menuAnchorNote = null
                }) {
                    Text("Cancel")
                }
            }
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
                    if (selectedNote!!.isReadOnly) "View Note" else "Edit Note"
                )
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2A2A2A))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = editText,
                            onValueChange = { newValue: String ->
                                if (selectedNote!!.isReadOnly.not()) editText = newValue
                            },
                            enabled = selectedNote!!.isReadOnly.not(),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (selectedNote!!.isReadOnly) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This note is read-only (max edits reached)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
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
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    selectedNote = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && selectedNote != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedNote = null
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
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
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    selectedNote = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val dateTime = note.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    val timeString = "${dateTime.date} ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
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
                color = Color.White
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
                    color = Color.Gray
                )
                if (note.isReadOnly) {
                    Text(
                        text = "Read-only",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "··· ${note.remainingEdits}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (note.remainingEdits) {
                            1 -> MaterialTheme.colorScheme.error
                            2 -> Color.Yellow
                            else -> Color.Gray
                        }
                    )
                }
            }
        }
    }
}
