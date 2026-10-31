package com.example.data.model

enum class CashType(val label: String) {
    INCOME("Income"),
    EXPENSE("Expense");

    companion object {
        fun fromString(value: String): CashType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: EXPENSE
        }
    }
}
