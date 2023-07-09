package com.example.habit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.habit.data.local.TypeConverters.DurationConverter
import com.example.habit.data.local.TypeConverters.LocalDateConverter
import com.example.habit.data.local.TypeConverters.LocalDateTimeConvertor
import com.example.habit.data.models.Habit

@Database(entities = [Habit::class], version = 2)
@TypeConverters(LocalDateConverter::class,DurationConverter::class,LocalDateTimeConvertor::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao:HabitDao
    companion object{
        const val dbName="Habit"
    }

}