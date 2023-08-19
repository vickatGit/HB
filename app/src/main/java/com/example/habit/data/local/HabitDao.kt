package com.example.habit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.GroupHabitsEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.local.entity.HabitGroupWithHabits
import com.example.habit.data.util.HabitGroupRecordSyncType
import com.example.habit.data.util.HabitRecordSyncType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addHabit(habits: List<HabitEntity>)


    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addGroupHabit(habits: List<GroupHabitsEntity>)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habits: HabitEntity)



    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType")
    fun getHabits(syncType: HabitRecordSyncType=HabitRecordSyncType.DeleteHabit):Flow<List<HabitEntity>>

    @Query("SELECT * FROM GroupHabitsEntity WHERE habitSyncType!=:syncType")
    fun getGroupHabits(syncType: HabitRecordSyncType=HabitRecordSyncType.DeleteHabit):Flow<List<HabitGroupWithHabits>>

    @Query("SELECT * FROM GroupHabitsEntity WHERE localId IN ( SELECT habitGroupId FROM HabitEntity WHERE userId=:userId ) ")
    fun getGroupHabitThumbs(userId:String):Flow<List<HabitGroupWithHabits>>


    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabit(habitId:String): HabitEntity?

    @Query("SELECT * FROM GroupHabitsEntity WHERE localId IN (SELECT habitGroupId FROM HabitEntity WHERE habitGroupId=:groupId)")
    suspend fun getGroupHabit(groupId:String):HabitGroupWithHabits

    @Query("SELECT * FROM HabitEntity WHERE endDate < :date")
    fun getCompletedHabits(date: LocalDate):Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType ")
    fun getUnSyncedHabits(syncType:HabitRecordSyncType = HabitRecordSyncType.SyncedHabit):Flow<List<HabitEntity>>

    @Query("SELECT * FROM GroupHabitsEntity WHERE habitSyncType!=:syncType ")
    fun getGroupUnSyncedHabits(syncType:HabitGroupRecordSyncType = HabitGroupRecordSyncType.SyncedHabit):Flow<List<GroupHabitsEntity>>

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

    @Query("DELETE FROM HabitEntity WHERE id=:habitId")
    suspend fun deleteHabit(habitId: String):Int

}