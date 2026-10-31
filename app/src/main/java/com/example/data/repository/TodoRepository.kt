package com.example.data.repository

import com.example.data.local.TodoDao
import com.example.data.model.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoEntity>> = todoDao.getAllTodos()
    val pendingTodos: Flow<List<TodoEntity>> = todoDao.getPendingTodos()
    val completedTodos: Flow<List<TodoEntity>> = todoDao.getCompletedTodos()

    fun getTodoById(id: Long): Flow<TodoEntity?> = todoDao.getTodoById(id)

    suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: TodoEntity) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: TodoEntity) = todoDao.deleteTodo(todo)

    suspend fun deleteTodoById(id: Long) = todoDao.deleteTodoById(id)

    suspend fun toggleCompletion(id: Long, isCompleted: Boolean) {
        todoDao.updateCompletionStatus(id, isCompleted)
    }
}
