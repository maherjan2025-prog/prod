package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CashType
import com.example.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)

    @TypeConverter
    fun fromCashType(cashType: CashType): String = cashType.name

    @TypeConverter
    fun toCashType(value: String): CashType = CashType.fromString(value)
}
