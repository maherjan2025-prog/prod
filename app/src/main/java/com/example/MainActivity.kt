package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.model.CashNoteEntity
import com.example.data.model.NoteEntity
import com.example.data.model.TodoEntity
import com.example.data.repository.CashNoteRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TodoRepository
import com.example.ui.MainViewModel
import com.example.ui.cash.CashNoteDialog
import com.example.ui.cash.CashNotesScreen
import com.example.ui.note.NoteDialog
import com.example.ui.note.NotesScreen
import com.example.ui.overview.OverviewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.todo.TodoDialog
import com.example.ui.todo.TodoScreen

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    OVERVIEW("Overview", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_overview"),
    TODOS("Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "tab_todos"),
    NOTES("Notes", Icons.Filled.Description, Icons.Outlined.Description, "tab_notes"),
    CASH("Cash", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "tab_cash")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val todoRepo = TodoRepository(db.todoDao())
        val noteRepo = NoteRepository(db.noteDao())
        val cashRepo = CashNoteRepository(db.cashNoteDao())
        MainViewModel.Factory(todoRepo, noteRepo, cashRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(AppTab.OVERVIEW) }

    // Interconnected flow: Logging cash note from a todo
    var cashDialogPrefilledTodo by remember { mutableStateOf<TodoEntity?>(null) }
    var showQuickTodoDialog by remember { mutableStateOf(false) }
    var showQuickNoteDialog by remember { mutableStateOf(false) }
    var showQuickCashDialog by remember { mutableStateOf(false) }

    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val cashNotes by viewModel.cashNotes.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()

    // Handle back button to return to Overview when on other tabs
    BackHandler(enabled = selectedTab != AppTab.OVERVIEW) {
        selectedTab = AppTab.OVERVIEW
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.OVERVIEW -> {
                OverviewScreen(
                    todos = todos,
                    notes = notes,
                    cashNotes = cashNotes,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    onNavigateToTodos = { selectedTab = AppTab.TODOS },
                    onNavigateToNotes = { selectedTab = AppTab.NOTES },
                    onNavigateToCash = { selectedTab = AppTab.CASH },
                    onQuickAddTask = { showQuickTodoDialog = true },
                    onQuickAddNote = { showQuickNoteDialog = true },
                    onQuickAddCash = { showQuickCashDialog = true },
                    onToggleTodo = { id, isDone ->
                        viewModel.toggleTodoCompletion(id, isDone)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.TODOS -> {
                TodoScreen(
                    todos = todos,
                    onSaveTodo = { viewModel.saveTodo(it) },
                    onToggleCompletion = { id, isDone -> viewModel.toggleTodoCompletion(id, isDone) },
                    onUpdateSubtasks = { todo, subtasks -> viewModel.updateTodoSubtasks(todo, subtasks) },
                    onDeleteTodo = { id -> viewModel.deleteTodo(id) },
                    onLogCashNoteFromTodo = { todo ->
                        cashDialogPrefilledTodo = todo
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.NOTES -> {
                NotesScreen(
                    notes = notes,
                    onSaveNote = { viewModel.saveNote(it) },
                    onTogglePin = { id, isPinned -> viewModel.toggleNotePin(id, isPinned) },
                    onDeleteNote = { id -> viewModel.deleteNote(id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.CASH -> {
                CashNotesScreen(
                    cashNotes = cashNotes,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    onSaveCashNote = { viewModel.saveCashNote(it) },
                    onDeleteCashNote = { id -> viewModel.deleteCashNote(id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // Quick Creation Dialogs triggered from Overview or cross-module
    if (cashDialogPrefilledTodo != null) {
        CashNoteDialog(
            initialCashNote = null,
            prefilledTodo = cashDialogPrefilledTodo,
            onDismiss = { cashDialogPrefilledTodo = null },
            onSave = { cashNote ->
                viewModel.saveCashNote(cashNote)
                cashDialogPrefilledTodo = null
            }
        )
    }

    if (showQuickTodoDialog) {
        TodoDialog(
            initialTodo = null,
            onDismiss = { showQuickTodoDialog = false },
            onSave = {
                viewModel.saveTodo(it)
                showQuickTodoDialog = false
            }
        )
    }

    if (showQuickNoteDialog) {
        NoteDialog(
            initialNote = null,
            onDismiss = { showQuickNoteDialog = false },
            onSave = {
                viewModel.saveNote(it)
                showQuickNoteDialog = false
            }
        )
    }

    if (showQuickCashDialog) {
        CashNoteDialog(
            initialCashNote = null,
            prefilledTodo = null,
            onDismiss = { showQuickCashDialog = false },
            onSave = {
                viewModel.saveCashNote(it)
                showQuickCashDialog = false
            }
        )
    }
}
