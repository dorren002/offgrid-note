package com.offgrid.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.offgrid.note.data.model.Note
import com.offgrid.note.ui.theme.LocalOffgridTheme

@Composable
fun EditCounter(
    note: Note,
    modifier: Modifier = Modifier
) {
    val theme = LocalOffgridTheme.current
    
    Row(modifier = modifier) {
        if (note.isReadOnly) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = theme.text.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        } else {
            val filledCount = Note.MAX_EDIT_COUNT - note.editCount
            
            repeat(Note.MAX_EDIT_COUNT) { index ->
                val isFilled = index < filledCount
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (isFilled) theme.border else theme.border.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}