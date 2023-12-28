package com.habitude.habit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habitude.habit.data.local.TypeConverters.DurationConverter
import com.habitude.habit.data.local.TypeConverters.EntryConverter
import com.habitude.habit.data.local.TypeConverters.EntryMapConverter
import com.habitude.habit.data.local.TypeConverters.LocalDateConverter
import com.habitude.habit.data.local.TypeConverters.LocalDateTimeConvertor
import com.habitude.habit.data.local.entity.GroupHabitsEntity
import com.habitude.habit.data.local.entity.HabitEntity

@Database(entities = [HabitEntity::class , GroupHabitsEntity::class], version = 16)
@TypeConverters(LocalDateConverter::class,DurationConverter::class,LocalDateTimeConvertor::class,EntryMapConverter::class,EntryConverter::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao:HabitDao
    companion object{
        const val dbName="Habit"
    }

}