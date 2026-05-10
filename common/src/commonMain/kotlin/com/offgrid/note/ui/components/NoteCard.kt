package com.offgrid.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offgrid.note.data.model.Note
import com.offgrid.note.ui.theme.LocalOffgridTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun NoteCard(
    note: Note,
    onEdit: () -> Unit,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val theme = LocalOffgridTheme.current
    val timezone = TimeZone.currentSystemDefault()
    val dateTime = try {
        note.createdAt.toLocalDateTime(timezone)
    } catch (e: Exception) {
        note.createdAt.toLocalDateTime(TimeZone.UTC)
    }
    val timezoneId = try { timezone.id } catch (e: Exception) { "UTC" }
    val timeString = "$timezoneId ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')} | ${dateTime.year}.${dateTime.monthNumber.toString().padStart(2, '0')}.${dateTime.dayOfMonth.toString().padStart(2, '0')}"
    
    val textColor = if (note.isReadOnly) theme.text.copy(alpha = 0.4f) else theme.text

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .clickable { onEdit() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isEditing && note.editCount == 2) {
                        theme.border.copy(alpha = 0.1f)
                    } else {
                        theme.background
                    }
                )
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = note.content,
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeString,
                    color = theme.text.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                
                EditCounter(note = note)
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(theme.border.copy(alpha = 0.4f))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DesktopNoteCard(
    note: Note,
    onEdit: () -> Unit,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val theme = LocalOffgridTheme.current
    val dateTime = note.createdAt.toLocalDateTime(TimeZone.UTC)
    val timeString = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    
    val textColor = if (note.isReadOnly) theme.text.copy(alpha = 0.4f) else theme.text

    Column(
        modifier = modifier
            .background(theme.background)
            .border(1.dp, theme.border.copy(alpha = 0.3f))
            .padding(12.dp)
            .clickable { onEdit() }
    ) {
        Text(
            text = note.content,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            maxLines = 6
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeString,
                color = theme.text.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            
            EditCounter(note = note)
        }
    }
}
