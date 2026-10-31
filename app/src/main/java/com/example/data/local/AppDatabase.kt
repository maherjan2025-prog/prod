package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CashNoteEntity
import com.example.data.model.CashType
import com.example.data.model.NoteEntity
import com.example.data.model.Priority
import com.example.data.model.TodoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TodoEntity::class, NoteEntity::class, CashNoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun noteDao(): NoteDao
    abstract fun cashNoteDao(): CashNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "omninote_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Prepopulate helpful initial records in background
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val now = System.currentTimeMillis()
            val oneDayMillis = 86_400_000L

            // 1. Initial Sample Todos
            val sampleTodos = listOf(
                TodoEntity(
                    title = "Review quarterly budget & ledger",
                    description = "Verify bank deposits, tax deductions, and reconcile cash notes receipts.",
                    isCompleted = false,
                    priority = Priority.URGENT,
                    dueDate = now + oneDayMillis,
                    category = "Finance",
                    subtasksRaw = "[x] Export monthly statement\n[ ] Match cash receipts with slips\n[ ] Allocate savings budget",
                    estimatedCost = 0.0,
                    createdAt = now - 3600_000,
                    updatedAt = now
                ),
                TodoEntity(
                    title = "Restock office supplies & ergonomic stand",
                    description = "Order mechanical keyboard wrist rest and printer cartridges.",
                    isCompleted = false,
                    priority = Priority.HIGH,
                    dueDate = now + (oneDayMillis * 2),
                    category = "Work",
                    subtasksRaw = "[ ] Research dual-monitor arm\n[ ] Check office stationery drawer",
                    estimatedCost = 145.0,
                    createdAt = now - 7200_000,
                    updatedAt = now
                ),
                TodoEntity(
                    title = "Weekly grocery & meal prep list",
                    description = "Fresh vegetables, olive oil, Greek yogurt, and sourdough.",
                    isCompleted = true,
                    priority = Priority.MEDIUM,
                    dueDate = now,
                    category = "Shopping",
                    subtasksRaw = "[x] Check fridge inventory\n[x] Buy almond milk & fruits\n[x] Pick up bakery order",
                    estimatedCost = 68.50,
                    createdAt = now - 14400_000,
                    updatedAt = now
                )
            )
            sampleTodos.forEach { db.todoDao().insertTodo(it) }

            // 2. Initial Sample Notes
            val sampleNotes = listOf(
                NoteEntity(
                    title = "Project Roadmap & Sprint Goals",
                    content = "Key milestones for the quarter:\n- Implement zero-latency SQLite persistence\n- Design Material 3 financial slips with barcode styling\n- Add instant cashflow calculations and category charts",
                    colorHex = "#FEF3C7", // Amber
                    isPinned = true,
                    tags = "Sprint,Architecture,Milestones",
                    folder = "Work",
                    createdAt = now - 86400_000,
                    updatedAt = now
                ),
                NoteEntity(
                    title = "Investment & Emergency Fund Strategy",
                    content = "Rule of thumb:\n1. Maintain 6 months of liquid expenses in high-yield account.\n2. Auto-transfer 20% of net freelance cash notes to growth index.\n3. Track all cash disbursements with reference slips.",
                    colorHex = "#D1FAE5", // Emerald
                    isPinned = true,
                    tags = "Finance,Savings,Rules",
                    folder = "Finance",
                    createdAt = now - (86400_000 * 2),
                    updatedAt = now
                ),
                NoteEntity(
                    title = "Books to Read & Study Notes",
                    content = "- Designing Data-Intensive Applications\n- The Psychology of Money by Morgan Housel\n- Atomic Habits (Habit stacking principles applied to daily to-dos)",
                    colorHex = "#E0F2FE", // Sky
                    isPinned = false,
                    tags = "Reading,Habits,Personal",
                    folder = "Ideas",
                    createdAt = now - (86400_000 * 3),
                    updatedAt = now
                )
            )
            sampleNotes.forEach { db.noteDao().insertNote(it) }

            // 3. Initial Sample Cash Notes
            val sampleCashNotes = listOf(
                CashNoteEntity(
                    title = "Client Project Milestone Payment",
                    amount = 2850.00,
                    type = CashType.INCOME,
                    category = "Salary",
                    paymentMethod = "Bank Transfer",
                    noteText = "Final deliverables approved for mobile redesign phase 1. Invoice #INV-2026-08.",
                    referenceSlip = "SLIP-98421-IN",
                    transactionDate = now - 18000_000
                ),
                CashNoteEntity(
                    title = "Cloud Infrastructure & Domain Hosting",
                    amount = 89.90,
                    type = CashType.EXPENSE,
                    category = "Bills & Utilities",
                    paymentMethod = "Credit Card",
                    noteText = "Monthly dedicated server cluster and CDN usage.",
                    referenceSlip = "RCP-77412-EXP",
                    transactionDate = now - 36000_000
                ),
                CashNoteEntity(
                    title = "Weekly Farmers Market & Groceries",
                    amount = 68.50,
                    type = CashType.EXPENSE,
                    category = "Food & Dining",
                    paymentMethod = "Cash",
                    noteText = "Organic greens, local honey, fresh bread and pantry essentials.",
                    referenceSlip = "CSH-55019-EXP",
                    relatedTodoTitle = "Weekly grocery & meal prep list",
                    transactionDate = now - 54000_000
                ),
                CashNoteEntity(
                    title = "Quarterly Dividend Payout",
                    amount = 412.30,
                    type = CashType.INCOME,
                    category = "Investment",
                    paymentMethod = "Bank Transfer",
                    noteText = "S&P 500 Dividend reinvestment cash balance distribution.",
                    referenceSlip = "DIV-30219-IN",
                    transactionDate = now - 72000_000
                )
            )
            sampleCashNotes.forEach { db.cashNoteDao().insertCashNote(it) }
        }
    }
}
