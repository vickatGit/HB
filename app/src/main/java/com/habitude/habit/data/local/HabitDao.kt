package com.habitude.habit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitude.habit.data.local.entity.EntryEntity
import com.habitude.habit.data.local.entity.GroupHabitsEntity
import com.habitude.habit.data.local.entity.HabitEntity
import com.habitude.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.habitude.habit.data.util.HabitGroupRecordSyncType
import com.habitude.habit.data.util.HabitRecordSyncType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface HabitDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addHabit(habits: List<HabitEntity>)


    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun addGroupHabit(habits: List<GroupHabitsEntity>)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habits: HabitEntity)

    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType AND habitGroupId IS NULL")
    fun getHabits(syncType: HabitRecordSyncType=HabitRecordSyncType.DeleteHabit):Flow<List<HabitEntity>>
    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType AND userId=:userId")
    suspend fun getHabitsForProgress(syncType: HabitRecordSyncType=HabitRecordSyncType.DeleteHabit,userId:String):List<HabitEntity>
    @Query("SELECT * FROM HabitEntity ")
    fun getsHabits():Flow<List<HabitEntity>>

    @Query("SELECT * FROM GroupHabitsEntity WHERE habitSyncType!=:syncType AND habitSyncType!=:nonMemberSyncType ")
    fun getGroupHabits(syncType: HabitRecordSyncType=HabitRecordSyncType.DeleteHabit,
                       nonMemberSyncType: HabitGroupRecordSyncType=HabitGroupRecordSyncType.REMOVED_USER_FROM_GROUP_HABIT
                       ):Flow<List<HabitGroupWithHabitsEntity>>


    //this query returning the all habits in future needs to be optimised
    @Query("SELECT * FROM GroupHabitsEntity WHERE localId IN ( SELECT habitGroupId FROM HabitEntity WHERE userId=:userId ) ")
    fun getGroupHabitThumbs(userId:String):Flow<List<HabitGroupWithHabitsEntity>>

//    @Query("select * from GroupHabitsEntity inner join HabitEntity on GroupHabitsEntity.localId=HabitEntity.habitGroupId where HabitEntity.userId = :userId")
//    fun getGroupHabitsThumbs(userId:String):Object
//
//    @Query("select * from GroupHabitsEntity inner join HabitEntity on GroupHabitsEntity.localId=HabitEntity.habitGroupId where HabitEntity.userId = :userId and GroupHabitsEntity.members LIKE '%' || :userId || '%'")
//    fun getGroupHabitThumbs(userId:String):Flow<List<HabitGroupWithHabitsEntity>>


    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabit(habitId:String): HabitEntity?

    @Query("SELECT * FROM GroupHabitsEntity WHERE localId IN (SELECT habitGroupLocalId FROM HabitEntity WHERE habitGroupLocalId=:groupId)")
    suspend fun getGroupHabit(groupId:String):HabitGroupWithHabitsEntity

    @Query("SELECT * FROM HabitEntity")
    fun getCompletedHabits():Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE habitSyncType!=:syncType ")
    fun getUnSyncedHabits(syncType:HabitRecordSyncType = HabitRecordSyncType.SyncedHabit):List<HabitEntity>

    @Query("SELECT * FROM GroupHabitsEntity WHERE habitSyncType!=:syncType ")
    fun getGroupUnSyncedHabits(syncType:HabitGroupRecordSyncType = HabitGroupRecordSyncType.SyncedHabit):List<GroupHabitsEntity>

    @Query("UPDATE HabitEntity SET entryList = :entryList, habitSyncType=:syncType WHERE id = :habitId")
    suspend fun updateHabitEntries(habitId: String,entryList : Map<LocalDate, EntryEntity>?,syncType:HabitRecordSyncType = HabitRecordSyncType.UpdateHabitEntries):Int


    @MapInfo(keyColumn = "entryList", valueColumn = "entryList")
    @Query("SELECT entryList FROM HabitEntity WHERE id = :habitId")
    suspend fun getHabitEntries(habitId: String):Map<LocalDate, EntryEntity>?

//    @Query("DELETE FROM HabitEntity WHERE id=:habitId")
//    suspend fun deleteHabit(habitId: String):Int

    @Query("DELETE FROM habitentity")
    suspend fun deleteAllHabits():Int

    @Query("DELETE FROM grouphabitsentity")
    suspend fun deleteAllGroupHabits():Int

    @Query("UPDATE HabitEntity SET habitSyncType=:shouldDelete WHERE id = :habitId")
    suspend fun updateDeleteStatus(habitId: String,shouldDelete:HabitRecordSyncType=HabitRecordSyncType.DeleteHabit):Int

    @Query("UPDATE HabitEntity SET habitSyncType=:habitEntriesSyncType WHERE serverId=:habitServerId")
    suspend fun updateHabitEntriesState(habitServerId:String,habitEntriesSyncType:HabitRecordSyncType)

    @Query("UPDATE GroupHabitsEntity SET habitSyncType=:shouldDelete WHERE localId = :habitId")
    suspend fun updateGroupHabitDeleteStatus(habitId: String,shouldDelete:HabitGroupRecordSyncType=HabitGroupRecordSyncType.DeleteHabit):Int

    @Query("UPDATE GroupHabitsEntity SET habitSyncType=:shouldDelete WHERE localId = :habitId")
    suspend fun updateRemoveGroupHabitDeleteStatus(habitId: String,shouldDelete:HabitGroupRecordSyncType=HabitGroupRecordSyncType.REMOVED_USER_FROM_GROUP_HABIT):Int


    @Query("DELETE FROM HabitEntity WHERE id=:habitId")
    suspend fun deleteHabit(habitId: String):Int

    @Query("DELETE FROM GroupHabitsEntity WHERE localId=:habitGroupId")
    suspend fun deleteGroupHabit(habitGroupId: String):Int

    @Query("UPDATE HabitEntity SET habitSyncType=:syncType WHERE habitGroupLocalId=:habitGroupId AND userId IN (:userIds)")
    suspend fun removeMembersFromGroupHabit(
        syncType: HabitRecordSyncType = HabitRecordSyncType.REMOVED_USER_FROM_GROUP_HABIT,
        habitGroupId: String,
        userIds: List<String>
        ):Int



    @Query("UPDATE GroupHabitsEntity SET " +
            "title=:title," +
            "description=:description,"+
            "startDate=:startDate,"+
            "endDate=:endDate,"+
            "isReminderOn=:isReminderOn,"+
            "reminderQuestion=:reminderQuestion,"+
            "habitSyncType=:syncType,"+
            "reminderTime=:reminderTime WHERE localId=:groupId"
    )
    suspend fun updateGroupHabit(
        groupId:String,
        title:String,
        description:String,
        startDate:LocalDate,
        endDate:LocalDate,
        isReminderOn:Boolean,
        reminderQuestion:String,
        reminderTime:LocalDateTime,
        syncType:HabitRecordSyncType = HabitRecordSyncType.UpdateHabit
    ):Int


    @Query("Select * FROM HabitEntity WHERE habitGroupId=:localId AND userId=:admin")
    suspend fun getGroupAdminHabit(admin: String?, localId: String):HabitEntity

}