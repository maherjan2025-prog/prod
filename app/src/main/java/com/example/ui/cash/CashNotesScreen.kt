package com.example.ui.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CashNoteEntity
import com.example.data.model.CashType
import com.example.ui.components.EmptyState
import com.example.ui.components.Formatters
import com.example.ui.components.SearchBar
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen

@Composable
fun CashNotesScreen(
    cashNotes: List<CashNoteEntity>,
    totalIncome: Double,
    totalExpense: Double,
    onSaveCashNote: (CashNoteEntity) -> Unit,
    onDeleteCashNote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") } // "All", "Expenses", "Income"
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSlipNote by remember { mutableStateOf<CashNoteEntity?>(null) }

    val netBalance = totalIncome - totalExpense
    val categories = listOf("All", "Food & Dining", "Bills & Utilities", "Salary", "Shopping", "Transport", "Investment")

    val filteredCashNotes = remember(cashNotes, searchQuery, selectedTypeFilter, selectedCategory) {
        cashNotes.filter { note ->
            val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                    note.noteText.contains(searchQuery, ignoreCase = true) ||
                    note.referenceSlip.contains(searchQuery, ignoreCase = true) ||
                    note.category.contains(searchQuery, ignoreCase = true)

            val matchesType = when (selectedTypeFilter) {
                "Expenses" -> note.type == CashType.EXPENSE
                "Income" -> note.type == CashType.INCOME
                else -> true
            }

            val matchesCat = if (selectedCategory == "All") true else note.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesType && matchesCat
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_cash_note_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Record Cash Note")
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

            // Financial Summary Hero Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cash_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NET CASH BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = Formatters.formatCurrency(netBalance),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = if (netBalance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Income & Expense Sub-cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Income
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IncomeGreen.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(IncomeGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Income", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = Formatters.formatCurrency(totalIncome),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IncomeGreen
                                    )
                                }
                            }
                        }

                        // Total Expense
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ExpenseRed.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(ExpenseRed, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Expense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = Formatters.formatCurrency(totalExpense),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseRed
                                    )
                                }
                            }
                        }
                    }

                    // Cashflow ratio bar
                    val totalTurnover = totalIncome + totalExpense
                    if (totalTurnover > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val incomeRatio = (totalIncome / totalTurnover).toFloat()
                        LinearProgressIndicator(
                            progress = { incomeRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = IncomeGreen,
                            trackColor = ExpenseRed
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Income: ${(incomeRatio * 100).toInt()}%",
                                fontSize = 10.sp,
                                color = IncomeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Expense: ${( (1f - incomeRatio) * 100 ).toInt()}%",
                                fontSize = 10.sp,
                                color = ExpenseRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search cash slips, merchants, notes...",
                testTag = "cash_search_field"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Tabs: All, Expenses, Income
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Expenses", "Income").forEach { tab ->
                    FilterChip(
                        selected = selectedTypeFilter == tab,
                        onClick = { selectedTypeFilter = tab },
                        label = { Text(tab, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("cash_tab_$tab")
                    )
                }

                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.testTag("cash_cat_filter_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cash Notes Ledger List
            if (filteredCashNotes.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Payments,
                    title = if (searchQuery.isNotBlank()) "No records match search" else "No Cash Notes Yet",
                    subtitle = if (searchQuery.isNotBlank()) "Try clear search" else "Tap '+' to record your first income or expense cash note with voucher slips."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("cash_notes_list")
                ) {
                    items(filteredCashNotes, key = { it.id }) { cashNote ->
                        CashNoteCard(
                            cashNote = cashNote,
                            onClick = { selectedSlipNote = cashNote },
                            onDelete = { onDeleteCashNote(cashNote.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CashNoteDialog(
            initialCashNote = null,
            prefilledTodo = null,
            onDismiss = { showAddDialog = false },
            onSave = { entity ->
                onSaveCashNote(entity)
                showAddDialog = false
            }
        )
    }

    selectedSlipNote?.let { note ->
        CashReceiptSlipDialog(
            cashNote = note,
            onDismiss = { selectedSlipNote = null }
        )
    }
}
