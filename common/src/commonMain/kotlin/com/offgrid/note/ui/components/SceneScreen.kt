package com.offgrid.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Scene
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.theme.LocalOffgridTheme

@Composable
fun SceneScreen(
    repository: NoteRepository,
    currentSceneId: String,
    onSceneSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalOffgridTheme.current
    var scenes by remember { mutableStateOf(repository.getAllScenes()) }
    var showAddScene by remember { mutableStateOf(false) }
    var newSceneName by remember { mutableStateOf("") }
    var editingSceneId by remember { mutableStateOf<String?>(null) }
    var editingSceneName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.text.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Scene Management",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = theme.text
                    )
                }
                IconButton(
                    onClick = { showAddScene = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Scene",
                        tint = theme.text.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(theme.border.copy(alpha = 0.3f))
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SceneItem(
                        scene = null,
                        isSelected = currentSceneId == Scene.DEFAULT_SCENE_ID,
                        isEditing = false,
                        onSelect = { onSceneSelect(Scene.DEFAULT_SCENE_ID) },
                        onEdit = {},
                        onDelete = {}
                    )
                }

                items(scenes, key = { it.id }) { scene ->
                    SceneItem(
                        scene = scene,
                        isSelected = currentSceneId == scene.id,
                        isEditing = editingSceneId == scene.id,
                        onSelect = { onSceneSelect(scene.id) },
                        onEdit = {
                            editingSceneId = scene.id
                            editingSceneName = scene.name
                        },
                        onDelete = {
                            repository.deleteScene(scene.id)
                            scenes = repository.getAllScenes()
                            if (currentSceneId == scene.id) {
                                onSceneSelect(Scene.DEFAULT_SCENE_ID)
                            }
                        },
                        onEditComplete = { newName ->
                            repository.updateScene(scene.id, newName)
                            scenes = repository.getAllScenes()
                            editingSceneId = null
                        },
                        editName = editingSceneName,
                        onEditNameChange = { editingSceneName = it }
                    )
                }
            }
        }

        if (showAddScene) {
            AddSceneDialog(
                onAdd = { name ->
                    repository.insertScene(name)
                    scenes = repository.getAllScenes()
                    showAddScene = false
                    newSceneName = ""
                },
                onCancel = {
                    showAddScene = false
                    newSceneName = ""
                },
                sceneName = newSceneName,
                onSceneNameChange = { newSceneName = it }
            )
        }
    }
}

@Composable
fun SceneItem(
    scene: Scene?,
    isSelected: Boolean,
    isEditing: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEditComplete: (String) -> Unit = {},
    editName: String = "",
    onEditNameChange: (String) -> Unit = {}
) {
    val theme = LocalOffgridTheme.current
    val sceneName = scene?.name ?: "OffGrid.Note"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                1.dp,
                if (isSelected) theme.border else theme.border.copy(alpha = 0.3f)
            )
            .clickable(enabled = !isEditing) { onSelect() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            BasicTextField(
                value = editName,
                onValueChange = onEditNameChange,
                textStyle = TextStyle(
                    color = theme.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(theme.border),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Save",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = theme.border,
                    modifier = Modifier.clickable { onEditComplete(editName) }
                )
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = theme.text.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { onEdit() }
                )
            }
        } else {
            Text(
                text = sceneName,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = if (isSelected) theme.text else theme.text.copy(alpha = 0.7f)
            )
            if (scene != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Edit",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = theme.text.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { onEdit() }
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = theme.text.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddSceneDialog(
    onAdd: (String) -> Unit,
    onCancel: () -> Unit,
    sceneName: String,
    onSceneNameChange: (String) -> Unit
) {
    val theme = LocalOffgridTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .background(theme.background)
                .border(1.dp, theme.border)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Scene",
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                color = theme.text
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, theme.border)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = sceneName,
                    onValueChange = onSceneNameChange,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = theme.text.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable { onCancel() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Add",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = if (sceneName.isNotBlank()) theme.border else theme.text.copy(alpha = 0.3f),
                    modifier = Modifier
                        .clickable(enabled = sceneName.isNotBlank()) { onAdd(sceneName) }
                        .padding(8.dp)
                )
            }
        }
    }
}
