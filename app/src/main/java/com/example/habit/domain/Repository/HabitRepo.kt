package com.example.habit.domain.Repository


import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.GroupHabitsEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.network.model.AddHabitResponseModel.AddHabitResponseModel
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.GroupHabit
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepo {
    suspend fun addHabit(habit: Habit)
    suspend fun addGroupHabit(habit: GroupHabit)
    suspend fun updateHabit(habit: Habit)
    suspend fun updateGroupHabit(habit: GroupHabit)
    suspend fun removeHabit(habitServerId :String? , habitId: String?):Int
    fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>>

    suspend fun getGroupHabits(coroutineScope: CoroutineScope): Flow<List<GroupHabitWithHabits>>
    suspend fun getHabit(habitId:String):Habit
    suspend fun getGroupHabit(groupId:String): GroupHabitWithHabits?

    suspend fun getGroupHabitFromRemote(groupId:String): Flow<Boolean>
    suspend fun getHabitEntries(habitId: String) : HashMap<LocalDate, Entry>?
    suspend fun updateHabitEntries(habitServerId:String?, habitId: String,entries:HashMap<LocalDate,Entry>) : Int
    suspend fun getHabitThumb(habitId: String): Habit
    fun getCompletedHabits(): Flow<List<HabitThumb>>
    fun getUnSyncedHabits(): List<HabitEntity>
    fun getGroupUnSyncedHabits(): List<GroupHabitsEntity>
    suspend fun deleteFromRemote(habitId: String, habitServerId: String?)
    suspend fun addOrUpdateHabitToRemote(habit: HabitEntity): Flow<AddHabitResponseModel?>
    suspend fun updateHabitToRemote(habit: HabitEntity)
    suspend fun updateHabitEntriesToRemote(habitServerId:String?, entryList: Map<LocalDate, EntryEntity>?)
    suspend fun deleteFromLocal(id: String): Int
    suspend fun addGroupHabitToRemote(fromGroupHabit: GroupHabitsEntity)
    suspend fun updateGroupHabitToRemote(groupHabit: GroupHabitsEntity)


    suspend fun removeGroupHabit(habitServerId: String?, habitId: String?): Int
    suspend fun deleteGroupHabitFromRemote(habitGroupId: String, habitGroupServerId: String?)
    suspend fun deleteGroupHabitFromLocal(habitGroupId: String): Int

    suspend fun removeMembersFromGroupHabit(
        habitGroupId: String,
        groupHabitServerId: String?,
        userIds: List<String>
    ) : Int
    suspend fun removedMembersFromGroupHabitFromRemote(habitGroupId: String?, userIds: List<String>)
    suspend fun addMembersToGroupHabitFromRemote(groupHabit: String?, userIds: List<String>): Flow<Boolean>
    suspend fun addMembersToGroupHabit(groupHabit: GroupHabit?, userIds: List<String>): Flow<Boolean>
    suspend fun getGroupAdminHabit(admin: String?, localId: String): HabitEntity
    suspend fun getHabitsForProgress(coroutineScope: CoroutineScope): List<HabitThumb>
}