package com.example.habit.domain.Repository


import com.example.habit.data.models.HabitEntity
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import java.time.LocalDate

interface HabitRepo {
    suspend fun addHabit(habit: Habit): Long
    suspend fun removeHabit(habit: Habit)
    fun getHabits(): Flow<List<HabitThumb>>
    suspend fun getHabit(habitId:Int):Habit
    suspend fun getHabitEntries(habitId: Int) : HashMap<LocalDate, Entry>?
    suspend fun updateHabitEntries(habitId: Int,entries:HashMap<LocalDate,Entry>) : Int

    suspend fun getHabitThumb(habitId: Int): Habit
}