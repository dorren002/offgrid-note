package com.offgrid.note.data.model

import kotlinx.datetime.Instant

data class Scene(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val sortOrder: Int = 0
) {
    companion object {
        const val DEFAULT_SCENE_ID = ""
    }
}