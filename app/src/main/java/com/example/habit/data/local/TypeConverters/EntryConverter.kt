package com.example.habit.data.local.TypeConverters

import androidx.room.TypeConverter
import com.example.habit.data.models.EntryEntity
import com.google.gson.Gson

object EntryConverter {
    @TypeConverter
    fun toEntity(entry:String):EntryEntity{
        return Gson().fromJson(entry,EntryEntity::class.java)
    }
    @TypeConverter
    fun fromEntity(entry:EntryEntity):String{
        return Gson().toJson(entry)
    }
}