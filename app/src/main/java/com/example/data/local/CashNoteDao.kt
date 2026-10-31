package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CashNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashNoteDao {
    @Query("SELECT * FROM cash_notes ORDER BY transactionDate DESC, id DESC")
    fun getAllCashNotes(): Flow<List<CashNoteEntity>>

    @Query("SELECT * FROM cash_notes WHERE type = :type ORDER BY transactionDate DESC")
    fun getCashNotesByType(type: String): Flow<List<CashNoteEntity>>

    @Query("SELECT * FROM cash_notes WHERE category = :category ORDER BY transactionDate DESC")
    fun getCashNotesByCategory(category: String): Flow<List<CashNoteEntity>>

    @Query("SELECT SUM(amount) FROM cash_notes WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM cash_notes WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashNote(cashNote: CashNoteEntity): Long

    @Update
    suspend fun updateCashNote(cashNote: CashNoteEntity)

    @Delete
    suspend fun deleteCashNote(cashNote: CashNoteEntity)

    @Query("DELETE FROM cash_notes WHERE id = :id")
    suspend fun deleteCashNoteById(id: Long)
}
