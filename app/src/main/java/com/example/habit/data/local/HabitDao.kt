package com.example.habit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.habit.data.models.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Upsert
    suspend fun addHabit(habit: HabitEntity):Long

    @Delete
    suspend fun removeHabit(habit: HabitEntity)

    @Query("SELECT * FROM HabitEntity")
    fun getHabits():Flow<List<HabitEntity>>
}