package com.offgrid.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.offgrid.note.database.OffgridDatabase
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.OffgridDesktopApp
import com.offgrid.note.ui.theme.OffgridTheme
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "offgrid.core"
    ) {
        val dbFile = File(System.getProperty("user.home"), ".offgrid/offgrid.db")
        dbFile.parentFile?.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        
        try {
            OffgridDatabase.Schema.create(driver)
        } catch (e: Exception) {
            println("Database already exists, skipping creation: ${e.message}")
        }
        
        val database = OffgridDatabase(driver)
        val repository = NoteRepository(database)

        OffgridTheme {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.ui.graphics.Color.Black
            ) {
                OffgridDesktopApp(repository)
            }
        }
    }
}