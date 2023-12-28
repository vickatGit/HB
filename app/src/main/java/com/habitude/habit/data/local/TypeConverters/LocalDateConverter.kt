package com.habitude.habit.data.local.TypeConverters

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.LocalDate

object LocalDateConverter {
    @JvmStatic
    @TypeConverter
    fun fromLocalDate(value: LocalDate): String {
        return Gson().toJson(value,LocalDate::class.java)
    }

    @JvmStatic
    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
      return Gson().fromJson(value,LocalDate::class.java)
    }
}