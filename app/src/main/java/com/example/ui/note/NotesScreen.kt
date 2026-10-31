package com.example.ui.note

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.SearchBar

@Composable
fun NotesScreen(
    notes: List<NoteEntity>,
    onSaveNote: (NoteEntity) -> Unit,
    onTogglePin: (Long, Boolean) -> Unit,
    onDeleteNote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf("All") }
    var showDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }

    val folders = listOf("All", "Ideas", "Work", "Finance", "Personal", "Projects")

    val filteredNotes = remember(notes, searchQuery, selectedFolder) {
        notes.filter { note ->
            val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                    note.content.contains(searchQuery, ignoreCase = true) ||
                    note.tags.contains(searchQuery, ignoreCase = true)

            val matchesFolder = if (selectedFolder == "All") true else note.folder.equals(selectedFolder, ignoreCase = true)

            matchesSearch && matchesFolder
        }
    }

    val pinnedNotes = filteredNotes.filter { it.isPinned }
    val otherNotes = filteredNotes.filter { !it.isPinned }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new note")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search notes, tags, contents...",
                testTag = "notes_search_field"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Folder selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                folders.forEach { f ->
                    FilterChip(
                        selected = selectedFolder == f,
                        onClick = { selectedFolder = f },
                        label = { Text(f, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("folder_filter_$f")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredNotes.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Description,
                    title = if (searchQuery.isNotBlank()) "No notes match query" else "Your notebook is empty",
                    subtitle = if (searchQuery.isNotBlank()) "Try another search term" else "Tap '+' to create your first color-coded note with tags and folder organization."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("notes_list")
                ) {
                    // Pinned notes section
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "PINNED NOTES (${pinnedNotes.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onTogglePin = { onTogglePin(note.id, !note.isPinned) },
                                onEdit = {
                                    editingNote = note
                                    showDialog = true
                                },
                                onDelete = { onDeleteNote(note.id) }
                            )
                        }
                    }

                    // Other notes section
                    if (otherNotes.isNotEmpty()) {
                        if (pinnedNotes.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ALL NOTES (${otherNotes.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        items(otherNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onTogglePin = { onTogglePin(note.id, !note.isPinned) },
                                onEdit = {
                                    editingNote = note
                                    showDialog = true
                                },
                                onDelete = { onDeleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        NoteDialog(
            initialNote = editingNote,
            onDismiss = {
                showDialog = false
                editingNote = null
            },
            onSave = { saved ->
                onSaveNote(saved)
                showDialog = false
                editingNote = null
            }
        )
    }
}
