package com.example.habit.data.local.TypeConverters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateTimeConvertor {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return value.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }
}