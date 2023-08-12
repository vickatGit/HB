package com.example.habit.domain.Repository


import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepo {
    suspend fun addHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun removeHabit(habitServerId :String? , habitId: String?):Int
    fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>>
    suspend fun getHabit(habitId:String):Habit
    suspend fun getHabitEntries(habitId: String) : HashMap<LocalDate, Entry>?
    suspend fun updateHabitEntries(habitServerId:String?, habitId: String,entries:HashMap<LocalDate,Entry>) : Int
    suspend fun getHabitThumb(habitId: String): Habit
    fun getCompletedHabits(): Flow<List<HabitThumb>>
    fun getUnSyncedHabits(): Flow<List<HabitEntity>>
    suspend fun deleteFromRemote(habitId: String, habitServerId: String?)
    suspend fun addOrUpdateHabitToRemote(habit: HabitEntity)
    suspend fun updateHabitToRemote(habit: HabitEntity)
    suspend fun updateHabitEntriesToRemote(habitServerId:String?, entryList: Map<LocalDate, EntryEntity>?)
    suspend fun deleteFromLocal(id: String): Int


}