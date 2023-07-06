package com.example.habit.domain.Repository

import com.example.habit.data.models.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepo {
    suspend fun addHabit(habit: Habit): Long
    suspend fun removeHabit(habit: Habit)
    fun getHabits(): Flow<List<Habit>>
}