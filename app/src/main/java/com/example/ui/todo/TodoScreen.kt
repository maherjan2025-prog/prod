package com.example.ui.todo

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TodoEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.SearchBar

@Composable
fun TodoScreen(
    todos: List<TodoEntity>,
    onSaveTodo: (TodoEntity) -> Unit,
    onToggleCompletion: (Long, Boolean) -> Unit,
    onUpdateSubtasks: (TodoEntity, List<Subtask>) -> Unit,
    onDeleteTodo: (Long) -> Unit,
    onLogCashNoteFromTodo: (TodoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Pending", "Completed", "Urgent/High", "Finance"
    var showDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoEntity?>(null) }

    val filterOptions = listOf("All", "Pending", "Completed", "Urgent/High", "Finance")

    val filteredTodos = remember(todos, searchQuery, selectedFilter) {
        todos.filter { todo ->
            val matchesSearch = todo.title.contains(searchQuery, ignoreCase = true) ||
                    todo.description.contains(searchQuery, ignoreCase = true) ||
                    todo.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Pending" -> !todo.isCompleted
                "Completed" -> todo.isCompleted
                "Urgent/High" -> todo.priority == Priority.HIGH || todo.priority == Priority.URGENT
                "Finance" -> todo.category.equals("Finance", ignoreCase = true) || (todo.estimatedCost != null && todo.estimatedCost > 0)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val totalCount = todos.size
    val completedCount = todos.count { it.isCompleted }
    val pendingCount = totalCount - completedCount

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTodo = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_todo_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new to-do")
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
                placeholder = "Search tasks, categories, finance...",
                testTag = "todo_search_field"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Stats Cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Column {
                            Text(text = "Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$totalCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Column {
                            Text(text = "Pending", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$pendingCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Column {
                            Text(text = "Done", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$completedCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("filter_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Todos List
            if (filteredTodos.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.AssignmentTurnedIn,
                    title = if (searchQuery.isNotBlank()) "No matching tasks" else "No tasks found",
                    subtitle = if (searchQuery.isNotBlank()) "Try adjusting your search terms" else "Tap '+' below to add your first to-do task with subtasks & SQLite persistence."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("todos_list")
                ) {
                    items(filteredTodos, key = { it.id }) { todo ->
                        TodoItemCard(
                            todo = todo,
                            onToggleCompletion = { isDone ->
                                onToggleCompletion(todo.id, isDone)
                            },
                            onUpdateSubtasks = { updatedSubtasks ->
                                onUpdateSubtasks(todo, updatedSubtasks)
                            },
                            onEdit = {
                                editingTodo = todo
                                showDialog = true
                            },
                            onDelete = {
                                onDeleteTodo(todo.id)
                            },
                            onLogCashNote = {
                                onLogCashNoteFromTodo(todo)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        TodoDialog(
            initialTodo = editingTodo,
            onDismiss = {
                showDialog = false
                editingTodo = null
            },
            onSave = { saved ->
                onSaveTodo(saved)
                showDialog = false
                editingTodo = null
            }
        )
    }
}
