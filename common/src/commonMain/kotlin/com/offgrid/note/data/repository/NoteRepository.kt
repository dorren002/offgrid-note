package com.offgrid.note.data.repository

import com.offgrid.note.data.model.Note
import com.offgrid.note.database.OffgridDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class NoteRepository(private val database: OffgridDatabase) {

    fun getAllNotes(): List<Note> {
        return database.offgridDatabaseQueries.selectAllNotes().executeAsList().map { row ->
            Note(
                id = row.id,
                content = row.content,
                createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.created_at),
                updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.updated_at),
                editCount = row.edit_count.toInt()
            )
        }
    }

    fun getNoteById(id: String): Note? {
        return database.offgridDatabaseQueries.selectNoteById(id).executeAsOneOrNull()?.let { row ->
            Note(
                id = row.id,
                content = row.content,
                createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.created_at),
                updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.updated_at),
                editCount = row.edit_count.toInt()
            )
        }
    }

    fun insertNote(content: String): Note {
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()
        val nowMillis = now.toEpochMilliseconds()

        database.offgridDatabaseQueries.insertNote(
            id = id,
            content = content,
            created_at = nowMillis,
            updated_at = nowMillis,
            edit_count = 0
        )

        return Note(
            id = id,
            content = content,
            createdAt = now,
            updatedAt = now,
            editCount = 0
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

    fun deleteNote(id: String) {
        database.offgridDatabaseQueries.deleteNote(id)
    }

    fun searchNotes(query: String): List<Note> {
        return database.offgridDatabaseQueries.searchNotes(query).executeAsList().map { row ->
            Note(
                id = row.id,
                content = row.content,
                createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.created_at),
                updatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(row.updated_at),
                editCount = row.edit_count.toInt()
            )
        }
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
            val dateTime = note.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
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
