package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val colorHex: String = "#F8FAFC",
    val isPinned: Boolean = false,
    val tags: String = "", // comma-separated e.g. "Work,Finance,Project"
    val folder: String = "General", // "General", "Ideas", "Finance", "Personal", "Work"
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getTagList(): List<String> {
        if (tags.isBlank()) return emptyList()
        return tags.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
