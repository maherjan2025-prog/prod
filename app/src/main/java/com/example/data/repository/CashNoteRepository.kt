package com.example.data.repository

import com.example.data.local.CashNoteDao
import com.example.data.model.CashNoteEntity
import kotlinx.coroutines.flow.Flow

class CashNoteRepository(private val cashNoteDao: CashNoteDao) {
    val allCashNotes: Flow<List<CashNoteEntity>> = cashNoteDao.getAllCashNotes()
    val totalIncome: Flow<Double?> = cashNoteDao.getTotalIncome()
    val totalExpense: Flow<Double?> = cashNoteDao.getTotalExpense()

    fun getCashNotesByType(type: String): Flow<List<CashNoteEntity>> = cashNoteDao.getCashNotesByType(type)

    fun getCashNotesByCategory(category: String): Flow<List<CashNoteEntity>> = cashNoteDao.getCashNotesByCategory(category)

    suspend fun insertCashNote(cashNote: CashNoteEntity): Long = cashNoteDao.insertCashNote(cashNote)

    suspend fun updateCashNote(cashNote: CashNoteEntity) = cashNoteDao.updateCashNote(cashNote)

    suspend fun deleteCashNote(cashNote: CashNoteEntity) = cashNoteDao.deleteCashNote(cashNote)

    suspend fun deleteCashNoteById(id: Long) = cashNoteDao.deleteCashNoteById(id)
}
