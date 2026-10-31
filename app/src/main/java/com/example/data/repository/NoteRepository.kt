package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllActiveNotes()
    val pinnedNotes: Flow<List<NoteEntity>> = noteDao.getPinnedNotes()

    fun getNotesByFolder(folder: String): Flow<List<NoteEntity>> = noteDao.getNotesByFolder(folder)

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun togglePin(id: Long, isPinned: Boolean) {
        noteDao.updatePinStatus(id, isPinned)
    }
}
