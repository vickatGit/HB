package com.example.habit.domain.Repository


import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow

interface HabitRepo {
    suspend fun addHabit(habit: Habit): Long
    suspend fun removeHabit(habit: Habit)
    fun getHabits(): Flow<List<HabitThumb>>
}