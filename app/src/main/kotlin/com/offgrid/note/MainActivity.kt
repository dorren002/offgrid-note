package com.offgrid.note

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import com.offgrid.note.data.repository.NoteRepository
import com.offgrid.note.ui.OffgridMobileApp
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        AndroidThemeStorage.init(this)

        val database = createDatabase(this)
        val repository = NoteRepository(database)

        setContent {
            OffgridMobileApp(
                repository = repository,
                onExportNotes = { markdown ->
                    exportNotesToFile(markdown)
                }
            )
        }
    }
    
    private fun exportNotesToFile(content: String) {
        try {
            val fileName = "offgrid_notes_${System.currentTimeMillis()}.md"
            val file = File(getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }
            
            // Share the file
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Offgrid Notes Export")
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Export Notes"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}