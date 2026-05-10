package com.offgrid.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.OffgridMobileApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = createDatabase(this)
        val repository = NoteRepository(database)

        setContent {
            OffgridMobileApp(repository)
        }
    }
}