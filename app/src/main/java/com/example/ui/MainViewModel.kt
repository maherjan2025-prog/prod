package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CashNoteEntity
import com.example.data.model.CashType
import com.example.data.model.NoteEntity
import com.example.data.model.Subtask
import com.example.data.model.TodoEntity
import com.example.data.repository.CashNoteRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val todoRepository: TodoRepository,
    private val noteRepository: NoteRepository,
    private val cashNoteRepository: CashNoteRepository
) : ViewModel() {

    // Todos reactive stream
    val todos: StateFlow<List<TodoEntity>> = todoRepository.allTodos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Notes reactive stream
    val notes: StateFlow<List<NoteEntity>> = noteRepository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Cash Notes reactive stream
    val cashNotes: StateFlow<List<CashNoteEntity>> = cashNoteRepository.allCashNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Financial calculations
    val totalIncome: StateFlow<Double> = cashNoteRepository.totalIncome
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalExpense: StateFlow<Double> = cashNoteRepository.totalExpense
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Todo Operations
    fun saveTodo(todo: TodoEntity) {
        viewModelScope.launch {
            if (todo.id == 0L) {
                todoRepository.insertTodo(todo)
            } else {
                todoRepository.updateTodo(todo)
            }
        }
    }

    fun toggleTodoCompletion(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.toggleCompletion(id, isCompleted)
        }
    }

    fun updateTodoSubtasks(todo: TodoEntity, subtasks: List<Subtask>) {
        viewModelScope.launch {
            val formatted = TodoEntity.formatSubtasks(subtasks)
            val allCompleted = subtasks.isNotEmpty() && subtasks.all { it.isCompleted }
            val updated = todo.copy(
                subtasksRaw = formatted,
                isCompleted = if (allCompleted) true else todo.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
            todoRepository.updateTodo(updated)
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            todoRepository.deleteTodoById(id)
        }
    }

    // Note Operations
    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            if (note.id == 0L) {
                noteRepository.insertNote(note)
            } else {
                noteRepository.updateNote(note)
            }
        }
    }

    fun toggleNotePin(id: Long, isPinned: Boolean) {
        viewModelScope.launch {
            noteRepository.togglePin(id, isPinned)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(id)
        }
    }

    // Cash Note Operations
    fun saveCashNote(cashNote: CashNoteEntity) {
        viewModelScope.launch {
            if (cashNote.id == 0L) {
                cashNoteRepository.insertCashNote(cashNote)
            } else {
                cashNoteRepository.updateCashNote(cashNote)
            }
        }
    }

    fun deleteCashNote(id: Long) {
        viewModelScope.launch {
            cashNoteRepository.deleteCashNoteById(id)
        }
    }

    class Factory(
        private val todoRepository: TodoRepository,
        private val noteRepository: NoteRepository,
        private val cashNoteRepository: CashNoteRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(todoRepository, noteRepository, cashNoteRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
