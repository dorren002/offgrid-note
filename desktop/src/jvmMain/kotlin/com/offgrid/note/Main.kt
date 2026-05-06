package com.offgrid.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.offgrid.note.database.OffgridDatabase
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.OffgridNoteApp
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Offgrid Note"
    ) {
        val dbFile = File(System.getProperty("user.home"), ".offgrid/offgrid.db")
        dbFile.parentFile?.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        OffgridDatabase.Schema.create(driver)
        val database = OffgridDatabase(driver)
        val repository = NoteRepository(database)

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = Color(0xFF6B4EFF),
                background = Color.Black,
                surface = Color(0xFF1A1A1A)
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                OffgridNoteApp(repository)
            }
        }
    }
}
