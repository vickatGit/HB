package com.example.habit.domain.Repository


import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepo {
    suspend fun addHabit(habit: Habit)
    suspend fun removeHabit(habitId: String):Int
    fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>>
    suspend fun getHabit(habitId:String):Habit
    suspend fun getHabitEntries(habitId: String) : HashMap<LocalDate, Entry>?
    suspend fun updateHabitEntries(habitId: String,entries:HashMap<LocalDate,Entry>) : Int
    suspend fun getHabitThumb(habitId: String): Habit
    fun getCompletedHabits(): Flow<List<HabitThumb>>


}