package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Subtask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val category: String = "Personal",
    val subtasksRaw: String = "", // Stored as pipe or line delimited: "[x] Subtask" or "[ ] Subtask"
    val estimatedCost: Double? = null, // Linked finance capability: if this task costs money
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getSubtasks(): List<Subtask> {
        if (subtasksRaw.isBlank()) return emptyList()
        return subtasksRaw.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                val trimmed = line.trim()
                val isDone = trimmed.startsWith("[x]", ignoreCase = true)
                val text = if (trimmed.length > 3) trimmed.substring(3).trim() else trimmed
                Subtask(title = text, isCompleted = isDone)
            }
    }

    companion object {
        fun formatSubtasks(subtasks: List<Subtask>): String {
            return subtasks.joinToString("\n") { sub ->
                val marker = if (sub.isCompleted) "[x]" else "[ ]"
                "$marker ${sub.title.trim()}"
            }
        }
    }
}
