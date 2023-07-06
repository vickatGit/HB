package com.example.habit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.habit.data.models.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Upsert
    suspend fun addHabit(habit: Habit):Long

    @Delete
    suspend fun removeHabit(habit: Habit)

    @Query("SELECT * FROM Habit")
    fun getHabits():Flow<List<Habit>>
}