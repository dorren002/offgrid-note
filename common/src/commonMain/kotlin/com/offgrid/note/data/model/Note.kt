package com.offgrid.note.data.model

import kotlinx.datetime.Instant

data class Note(
    val id: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val editCount: Int
) {
    companion object {
        const val MAX_EDIT_COUNT = 3
    }

    val isReadOnly: Boolean
        get() = editCount >= MAX_EDIT_COUNT

    val remainingEdits: Int
        get() = maxOf(0, MAX_EDIT_COUNT - editCount)
}