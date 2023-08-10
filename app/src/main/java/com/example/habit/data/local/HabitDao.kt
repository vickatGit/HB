package com.example.habit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.util.HabitRecordSyncType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addHabit(habits: List<HabitEntity>)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habits: HabitEntity)



    @Query("SELECT * FROM HabitEntity")
    fun getHabits():Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabit(habitId:String): HabitEntity?

    @Query("SELECT * FROM HabitEntity WHERE endDate < :date")
    fun getCompletedHabits(date: LocalDate):Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType ")
    fun getUnSyncedHabits(syncType:HabitRecordSyncType = HabitRecordSyncType.SyncedHabit):Flow<List<HabitEntity>>

    @Query("UPDATE HabitEntity SET entryList = :entryList, habitSyncType=:syncType WHERE id = :habitId")
    suspend fun updateHabitEntries(habitId: String,entryList : Map<LocalDate, EntryEntity>?,syncType:HabitRecordSyncType = HabitRecordSyncType.UpdateHabitEntries):Int


    @MapInfo(keyColumn = "entryList", valueColumn = "entryList")
    @Query("SELECT entryList FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabitEntries(habitId: String):Map<LocalDate, EntryEntity>?

//    @Query("DELETE FROM HabitEntity WHERE id=:habitId")
//    suspend fun deleteHabit(habitId: String):Int

    @Query("DELETE FROM habitentity")
    suspend fun deleteAllHabits():Int

    @Query("UPDATE HabitEntity SET habitSyncType=:shouldDelete WHERE id = :habitId")
    suspend fun updateDeleteStatus(habitId: String,shouldDelete:HabitRecordSyncType=HabitRecordSyncType.DeleteHabit):Int

}