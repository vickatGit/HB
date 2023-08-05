package com.example.habit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addHabit(habit: HabitEntity):Long


    @Query("SELECT * FROM HabitEntity")
    fun getHabits():Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabit(habitId:Int): HabitEntity?

    @Query("SELECT * FROM HabitEntity WHERE endDate < :date")
    fun getCompletedHabits(date: LocalDate):Flow<List<HabitEntity>>

    @Query("UPDATE HabitEntity SET entryList = :entryList WHERE id = :habitId")
    suspend fun updateHabitEntries(habitId: Int,entryList : Map<LocalDate, EntryEntity>?):Int

    @MapInfo(keyColumn = "entryList", valueColumn = "entryList")
    @Query("SELECT entryList FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabitEntries(habitId: Int):Map<LocalDate, EntryEntity>?

    @Query("DELETE FROM HabitEntity WHERE id=:habitId")
    suspend fun deleteHabit(habitId: Int):Int

}