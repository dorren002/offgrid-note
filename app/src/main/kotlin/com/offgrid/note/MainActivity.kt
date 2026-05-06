package com.offgrid.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.offgrid.note.database.createDatabase
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.OffgridNoteApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = createDatabase(this)
        val repository = NoteRepository(database)

        setContent {
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
}
