package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_notes")
data class CashNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: CashType = CashType.EXPENSE,
    val category: String = "General",
    val paymentMethod: String = "Cash", // "Cash", "Card", "Bank Transfer", "Wallet", "Crypto"
    val noteText: String = "",
    val referenceSlip: String = "", // e.g. receipt or slip #
    val transactionDate: Long = System.currentTimeMillis(),
    val relatedTodoTitle: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
