package com.offgrid.note.data.repository

import com.offgrid.note.data.model.Note
import com.offgrid.note.data.model.Scene
import com.offgrid.note.database.OffgridDatabase
import com.offgrid.note.database.Notes
import com.offgrid.note.database.Scenes
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class NoteRepository(private val database: OffgridDatabase) {

    private fun Notes.toNote(): Note {
        return Note(
            id = this.id,
            content = this.content,
            createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(this.created_at),
            updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(this.updated_at),
            editCount = this.edit_count.toInt(),
            sceneId = this.scene_id
        )
    }

    private fun Scenes.toScene(): Scene {
        return Scene(
            id = this.id,
            name = this.name,
            createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(this.created_at),
            sortOrder = this.sort_order.toInt()
        )
    }

    fun getAllNotes(): List<Note> {
        return database.offgridDatabaseQueries.selectAllNotes().executeAsList().map { it.toNote() }
    }

    fun getNotesByScene(sceneId: String): List<Note> {
        return database.offgridDatabaseQueries.selectNotesByScene(sceneId).executeAsList().map { it.toNote() }
    }

    fun getNoteById(id: String): Note? {
        return database.offgridDatabaseQueries.selectNoteById(id).executeAsOneOrNull()?.toNote()
    }

    fun insertNote(content: String, sceneId: String = ""): Note {
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()
        val nowMillis = now.toEpochMilliseconds()

        database.offgridDatabaseQueries.insertNote(
            id = id,
            content = content,
            created_at = nowMillis,
            updated_at = nowMillis,
            edit_count = 0,
            scene_id = sceneId
        )

        return Note(
            id = id,
            content = content,
            createdAt = now,
            updatedAt = now,
            editCount = 0,
            sceneId = sceneId
        )
    }

    fun updateNote(id: String, newContent: String): Boolean {
        val note = getNoteById(id) ?: return false
        if (note.isReadOnly) return false

        val now = Clock.System.now()
        database.offgridDatabaseQueries.updateNote(
            content = newContent,
            updated_at = now.toEpochMilliseconds(),
            id = id
        )
        return true
    }
    
    fun forceUpdateNote(id: String, newContent: String): Boolean {
        val note = getNoteById(id) ?: return false
        
        val now = Clock.System.now()
        database.offgridDatabaseQueries.updateNote(
            content = newContent,
            updated_at = now.toEpochMilliseconds(),
            id = id
        )
        return true
    }

    fun deleteNote(id: String) {
        database.offgridDatabaseQueries.deleteNote(id)
    }

    fun searchNotes(query: String): List<Note> {
        return database.offgridDatabaseQueries.searchNotes(query).executeAsList().map { it.toNote() }
    }

    // Scene functions
    fun getAllScenes(): List<Scene> {
        return database.offgridDatabaseQueries.selectAllScenes().executeAsList().map { it.toScene() }
    }

    fun getSceneById(id: String): Scene? {
        return database.offgridDatabaseQueries.selectSceneById(id).executeAsOneOrNull()?.toScene()
    }

    fun insertScene(name: String): Scene {
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()
        val allScenes = getAllScenes()
        val sortOrder = allScenes.size

        database.offgridDatabaseQueries.insertScene(
            id = id,
            name = name,
            created_at = now.toEpochMilliseconds(),
            sort_order = sortOrder.toLong()
        )

        return Scene(
            id = id,
            name = name,
            createdAt = now,
            sortOrder = sortOrder
        )
    }

    fun updateScene(id: String, newName: String): Boolean {
        database.offgridDatabaseQueries.updateScene(
            name = newName,
            id = id
        )
        return true
    }

    fun deleteScene(id: String) {
        database.offgridDatabaseQueries.deleteScene(id)
    }

    fun exportToMarkdown(): String {
        val notes = getAllNotes()
        val builder = StringBuilder()
        builder.appendLine("# Offgrid Notes Export")
        builder.appendLine()
        builder.appendLine("Exported on: ${Clock.System.now()}")
        builder.appendLine()
        builder.appendLine("---")
        builder.appendLine()

        notes.forEach { note ->
            val dateTime = try {
                note.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            } catch (e: Exception) {
                note.createdAt.toLocalDateTime(TimeZone.UTC)
            }
            builder.appendLine("## ${dateTime.date} ${dateTime.time}")
            builder.appendLine()
            builder.appendLine(note.content)
            builder.appendLine()
            if (note.isReadOnly) {
                builder.appendLine("*Read-only (max edits reached)*")
                builder.appendLine()
            }
            builder.appendLine("---")
            builder.appendLine()
        }

        return builder.toString()
    }
}