package com.offgrid.note.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.data.model.Scene
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.components.EditScreen
import com.offgrid.note.ui.components.NoteCard
import com.offgrid.note.ui.components.SceneScreen
import com.offgrid.note.ui.components.SettingsScreen
import com.offgrid.note.ui.theme.LocalOffgridTheme
import com.offgrid.note.ui.theme.OffgridThemeData
import com.offgrid.note.ui.theme.ThemeStorage

@Composable
fun OffgridMobileApp(
    repository: NoteRepository,
    onExportNotes: (String) -> Unit
) {
    var currentSceneId by remember { mutableStateOf(Scene.DEFAULT_SCENE_ID) }
    var notes by remember { mutableStateOf(repository.getNotesByScene(currentSceneId)) }
    var scenes by remember { mutableStateOf(repository.getAllScenes()) }
    var inputText by remember { mutableStateOf("") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSceneDialog by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(OffgridThemeData.Default) }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorVisible = true
            kotlinx.coroutines.delay(530)
            cursorVisible = false
            kotlinx.coroutines.delay(400)
        }
    }
    
    LaunchedEffect(Unit) {
        val savedTheme = ThemeStorage.loadTheme()
        if (savedTheme != null) {
            currentTheme = savedTheme
        }
    }

    // Load saved scene ID on start
    LaunchedEffect(Unit) {
        val savedSceneId = ThemeStorage.loadSceneId()
        if (savedSceneId != null) {
            currentSceneId = savedSceneId
        }
    }

    // Refresh notes when scene changes
    LaunchedEffect(currentSceneId) {
        notes = repository.getNotesByScene(currentSceneId)
        scenes = repository.getAllScenes()
    }

    CompositionLocalProvider(LocalOffgridTheme provides currentTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentTheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(
                    theme = currentTheme,
                    currentSceneId = currentSceneId,
                    scenes = scenes,
                    onSceneClick = { showSceneDialog = true },
                    onSettingsClick = { showSettingsDialog = true }
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 10.dp),
                    reverseLayout = true
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

                BottomArea(
                    theme = currentTheme,
                    inputText = inputText,
                    cursorVisible = cursorVisible,
                    onInputChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            repository.insertNote(inputText.trim(), currentSceneId)
                            notes = repository.getNotesByScene(currentSceneId)
                            inputText = ""
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    }
                )
            }

            val currentNote = selectedNote
            
            if (showEditDialog && currentNote != null) {
                EditScreen(
                    note = currentNote,
                    onSave = { newContent ->
                        repository.updateNote(currentNote.id, newContent)
                        notes = repository.getNotesByScene(currentSceneId)
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
                        notes = repository.getNotesByScene(currentSceneId)
                        editingNoteId = null
                        showEditDialog = false
                        selectedNote = null
                    }
                )
            }

            if (showSettingsDialog) {
                SettingsScreen(
                    currentTheme = currentTheme,
                    repository = repository,
                    onThemeChange = { newTheme -> 
                        currentTheme = newTheme
                        ThemeStorage.saveTheme(newTheme)
                    },
                    onExportNotes = onExportNotes,
                    onDismiss = { showSettingsDialog = false }
                )
            }

            if (showSceneDialog) {
                SceneScreen(
                    repository = repository,
                    currentSceneId = currentSceneId,
                    onSceneSelect = { newSceneId ->
                        currentSceneId = newSceneId
                        ThemeStorage.saveSceneId(newSceneId)
                        showSceneDialog = false
                    },
                    onDismiss = { showSceneDialog = false }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    theme: OffgridThemeData,
    currentSceneId: String,
    scenes: List<Scene>,
    onSceneClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSceneName = when {
        currentSceneId == Scene.DEFAULT_SCENE_ID -> "OffGrid.Note"
        else -> scenes.find { it.id == currentSceneId }?.name ?: "OffGrid.Note"
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSceneClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Scenes",
                        tint = theme.text.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentSceneName,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = theme.border
                )
            }
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