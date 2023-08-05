package com.example.habit.data.local.TypeConverters

import android.util.Log
import androidx.room.TypeConverter
import com.example.habit.data.local.entity.EntryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeParseException


object EntryMapConverter {

    @JvmStatic
    @TypeConverter
    fun fromEntries(entries: Map<LocalDate, EntryEntity>?): String? {
        return if (entries != null) {
            Gson().toJson(entries)
        } else {
            null
        }

    }

    @JvmStatic
    @TypeConverter
    fun toEntries(entries: String?): Map<LocalDate, EntryEntity>? {
        return if (entries != null) {
            val convertedMapType = object : TypeToken<Map<String, EntryEntity>?>() {}.type
            val habitEntries: MutableMap<LocalDate, EntryEntity> = mutableMapOf()
            val convertedMap =
                Gson().fromJson(entries, convertedMapType) as Map<String, EntryEntity>
            for ((key, value) in convertedMap) {
                try {
                    val localDate = LocalDate.parse(key)
                    habitEntries[localDate] = value
                } catch (e: DateTimeParseException) {
                    Log.e("TAG", "toEntries: catch $key")
                }
            }
            habitEntries
        } else {
            null
        }
    }


}