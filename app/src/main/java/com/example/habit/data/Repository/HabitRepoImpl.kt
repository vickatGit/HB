package com.example.habit.data.Repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.habit.data.Mapper.GroupHabitMapper.GroupHabitMapper
import com.example.habit.data.Mapper.HabitMapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper.HabitMapper
import com.example.habit.data.common.Connectivity
import com.example.habit.data.local.HabitDao
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.GroupHabitsEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.network.HabitApi
import com.example.habit.data.network.model.AddHabitResponseModel.AddHabitResponseModel
import com.example.habit.data.network.model.GroupHabitModel.GroupHabitsModel
import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import com.example.habit.data.network.model.UpdateHabitEntriesModel.EntriesModel
import com.example.habit.data.network.model.UserIdsModel.UserIdsModel
import com.example.habit.data.util.HabitGroupRecordSyncType
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.GroupHabit
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.example.habit.domain.models.Member
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.UUID

class HabitRepoImpl(
    private val habitDao: HabitDao,
    private val habitMapper: HabitMapper,
    private val entryMapper: EntryMapper,
    private val habitApi: HabitApi,
    private val connectivityManager: ConnectivityManager,
    private val groupHabitMapper: GroupHabitMapper,
    private val authPref: AuthPref,
    private val context: Context
) : HabitRepo {
    override suspend fun addHabit(habit: Habit) {
        habitDao.addHabit(listOf(habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)))
        if (Connectivity.isInternetConnected(context))
            addOrUpdateHabitToRemote(
                habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)
            ).collectLatest { Log.e("TAG", "addHabit: Added to server successfully $it") }
    }

    override suspend fun addGroupHabit(habit: GroupHabit) {
        val groupHabit = groupHabitMapper.fromGroupHabit(habit, HabitGroupRecordSyncType.AddHabit)
        groupHabit.admin = authPref.getUserId()
        val members = listOf(Member(userId = authPref.getUserId(), username = ""))
        groupHabit.members = Gson().toJson(members)
        habitDao.addGroupHabit(listOf(groupHabit))
        habitDao.addHabit(
            listOf(
                HabitEntity(
                    UUID.randomUUID().toString(),
                    null,
                    groupHabit.title,
                    groupHabit.description,
                    groupHabit.reminderQuestion,
                    groupHabit.startDate,
                    groupHabit.endDate,
                    groupHabit.isReminderOn,
                    groupHabit.reminderTime,
                    null,
                    HabitRecordSyncType.ADD_ADMIN_MEMBER_HABIT,
                    habit.id,
                    authPref.getUserId()
                )
            )
        )
        if (Connectivity.isInternetConnected(context)) {
            habit.admin = authPref.getUserId()
            addGroupHabitToRemote(
                groupHabitMapper.fromGroupHabit(
                    habit,
                    HabitGroupRecordSyncType.AddHabit
                )
            )
        }
    }


    override suspend fun updateHabit(habit: Habit) {
        val habitEntity = habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)
        habitEntity.habitSyncType = HabitRecordSyncType.UpdateHabit
        habitDao.updateHabit(habitEntity)
        if (Connectivity.isInternetConnected(context))
            updateHabitToRemote(habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit))
    }

    override suspend fun updateGroupHabit(habit: GroupHabit) {
        val groupHabit = groupHabitMapper.fromGroupHabit(habit, HabitGroupRecordSyncType.UpdateHabit)
        habitDao.updateGroupHabit(
            groupHabit.localId,
            groupHabit.title!!,
            groupHabit.description!!,
            groupHabit.startDate!!,
            groupHabit.endDate!!,
            groupHabit.isReminderOn!!,
            groupHabit.reminderQuestion!!,
            groupHabit.reminderTime!!
        )
        if (Connectivity.isInternetConnected(context)) {
            updateGroupHabitToRemote(groupHabit)
        }
    }


    override suspend fun removeHabit(habitServerId: String?, habitId: String?): Int {
        val res = habitDao.updateDeleteStatus(habitId!!)
        if (Connectivity.isInternetConnected(context)) {
            deleteFromRemote(habitId, habitServerId)
        }
        return res
    }

    override suspend fun removeGroupHabit(groupHabitServerId: String?, groupHabitId: String?): Int {
        val res = habitDao.updateGroupHabitDeleteStatus(groupHabitId!!)
        if (Connectivity.isInternetConnected(context)) {
            deleteGroupHabitFromRemote(groupHabitId, groupHabitServerId)
        }
        return res
    }

    override fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>> {
        Log.e("TAG", "onResponse: getHabits local")
        if (Connectivity.isInternetConnected(context)) {
            habitApi.getHabits().enqueue(object : Callback<HabitsListModel> {
                override fun onResponse(
                    call: Call<HabitsListModel>,
                    response: Response<HabitsListModel>
                ) {
                    if (response.code() == 200) {
                        Log.e("TAG", "onResponse: getHabits")
                        val habitList = response.body()
                        val habits = habitList!!.data.map { habit ->
                            habitMapper.mapToHabitEntityFromHabitModel(habit)
                        }
                        Log.e("TAG", "onResponse: habits get $habits")
                        coroutineScope.launch {
                            habitDao.addHabit(habits)
                        }
                    }
                }

                override fun onFailure(call: Call<HabitsListModel>, t: Throwable) {
                    Log.e("TAG", "onFailure: ${t.printStackTrace()}")
                }
            })
        }
        return habitDao.getHabits().map {
            it.map {
                habitMapper.mapFromHabitEntity(it)
            }
        }
    }


    override suspend fun getGroupHabits(coroutineScope: CoroutineScope): Flow<List<GroupHabitWithHabits>> {


        if (Connectivity.isInternetConnected(context)) {
            getHabits(coroutineScope)
            habitApi.getGroupHabits().enqueue(object : Callback<GroupHabitsModel> {
                override fun onResponse(
                    call: Call<GroupHabitsModel>,
                    response: Response<GroupHabitsModel>
                ) {
                    response.body()?.let {
                        var habitsList: List<HabitEntity> = listOf()
                        val groupHabits = response.body()?.let {
                            it.data?.map { groupHabits ->
                                groupHabits.let { groupHabit ->
                                    val habits = groupHabit!!.habits
                                    habitsList = habits!!.map {
                                        it?.startDate = groupHabit.startDate
                                        it?.endDate = groupHabit.endDate
                                        it?.description = groupHabit.description
                                        it?.title = groupHabit.title
                                        it?.habitGroupId = groupHabit.id
                                        it?.habitGroupLoacalId = groupHabit.localId
                                        habitMapper.mapToHabitEntityFromHabitModel(it!!)
                                    }
                                    groupHabitMapper.toGroupHabitModel(groupHabit!!)
                                }
                            }
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            habitDao.deleteAllGroupHabits()
                            groupHabits?.let { habitDao.addGroupHabit(groupHabits) }
                            habitDao.addHabit(habitsList)
                        }
                    }
                }

                override fun onFailure(call: Call<GroupHabitsModel>, t: Throwable) {
                    Log.e("TAG", "onFailure: getGroupHabits ${t.stackTrace}")
                }

            })
        }
        return habitDao.getGroupHabits().map { groupHabits ->
            groupHabits.map { groupHabit ->
                val userHabit = groupHabit.habits.find { it.userId == authPref.getUserId() }
                userHabit?.let { groupHabit.habits = listOf(userHabit) }
                groupHabitMapper.toGroupHabitWithHabits(groupHabit)
            }
        }

    }

    override suspend fun getHabit(habitId: String): Habit {
        return habitDao.getHabit(habitId).let {
            habitMapper.mapToHabit(habitDao.getHabit(habitId))
        }
    }

    override suspend fun getGroupHabit(groupId: String): GroupHabitWithHabits? {
        val res = habitDao.getGroupHabit(groupId)
        Log.e("TAG", "getGroupHabit: repo $res", )
        return res?.let { groupHabitMapper.toGroupHabitWithHabits(res) }
    }

    override suspend fun getGroupHabitFromRemote(groupId: String): Flow<Boolean> {
        return flow {
            val response = habitApi.getGroupHabit(groupId)
            if (response.isSuccessful) {
                response.body()?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        habitDao.addGroupHabit(listOf(groupHabitMapper.toGroupHabitModel(it.data!!)))
                        emit(true)
                    }
                }
            } else {
                emit(false)
            }
        }
    }


    override suspend fun getHabitThumb(habitId: String): Habit {
        return habitDao.getHabit(habitId).let {
            habitMapper.mapToHabit(habitDao.getHabit(habitId))
        }
    }

    override fun getCompletedHabits(): Flow<List<HabitThumb>> {
        return habitDao.getCompletedHabits(LocalDate.now()).map {
            it.map {
                habitMapper.mapFromHabitEntity(it)
            }
        }
    }

    override fun getUnSyncedHabits(): List<HabitEntity> {
        return habitDao.getUnSyncedHabits()
    }

    override fun getGroupUnSyncedHabits(): List<GroupHabitsEntity> {
        Log.e("TAG", "getGroupUnSyncedHabits: repo ")
        return habitDao.getGroupUnSyncedHabits()
    }


    override suspend fun deleteFromRemote(habitId: String, habitServerId: String?) {
        if (habitServerId != null) {
            habitApi.deleteHabit(habitServerId).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: deleteFromRemote $response")
                    if (response.code() == 200) {
                        CoroutineScope(Dispatchers.IO).launch {
                            habitDao.updateDeleteStatus(
                                habitId = habitId,
                                shouldDelete = HabitRecordSyncType.DeletedHabit
                            )
                            deleteFromLocal(habitId)
                        }
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("TAG", "onFailure: deleteFromRemote")
                }
            })
        } else {
            deleteFromLocal(habitId)
        }
    }

    override suspend fun deleteGroupHabitFromRemote(
        habitGroupId: String,
        habitGroupServerId: String?
    ) {
        if (habitGroupServerId != null) {
            habitApi.deleteGroupHabit(habitGroupServerId).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: deleteHabitGroupFromRemote $response")
                    if (response.code() == 200) {
                        CoroutineScope(Dispatchers.IO).launch {
                            habitDao.updateGroupHabitDeleteStatus(
                                habitId = habitGroupId,
                                shouldDelete = HabitGroupRecordSyncType.DeletedHabit
                            )
                            deleteGroupHabitFromLocal(habitGroupId)
                        }
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("TAG", "onFailure: deleteFromRemote")
                }
            })
        } else {
            deleteGroupHabitFromLocal(habitGroupId)
        }
    }

    override suspend fun addOrUpdateHabitToRemote(habit: HabitEntity): Flow<AddHabitResponseModel?> {
        Log.e("grp", "addOrUpdateHabitToRemote: out $habit ")
        return flow {
            val response = habitApi.addHabit(habitMapper.mapHabitModelToFromHabitEntity(habit))

            if (response.isSuccessful) {
                emit(response.body())
                Log.e("grp", "addOrUpdateHabitToRemote: response ${response.body()}")
            } else {
                emit(response.body())
            }
        }
    }

    override suspend fun addGroupHabitToRemote(habitEntity: GroupHabitsEntity) {


        //here is the bug
        CoroutineScope(Dispatchers.IO).launch {
        val groupHabit = getGroupAdminHabit(habitEntity.admin, habitEntity.localId)
        addOrUpdateHabitToRemote(groupHabit).collect {
            it?.let {
                habitApi.addGroupHabit(groupHabitMapper.toGroupHabitEntity(habitEntity), it.habitId)
                    .enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if(response.isSuccessful)
                                CoroutineScope(Dispatchers.IO).launch {
                                    getGroupHabits(this)
                                }
                        }
                        override fun onFailure(call: Call<Any>, t: Throwable) {}
                    })
            }
        }
        }
    }

    override suspend fun updateHabitToRemote(habit: HabitEntity) {
        if (habit.serverId != null) {
            habitApi.updateHabit(habitMapper.mapHabitModelToFromHabitEntity(habit), habit.serverId)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        Log.e("TAG", "onResponse: updateHabitToRemote $response")
                        if (response.code() == 200) {
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Log.e("TAG", "onFailure: updateHabitToRemote ${t.localizedMessage}")
                    }
                })
        } else {
            //In case of Habit is Added and updated without internet access (habit is not added to server due no internet)
            addOrUpdateHabitToRemote(habit).collectLatest { }
        }
    }


    override suspend fun updateHabitEntriesToRemote(
        habitServerId: String?,
        entryList: Map<LocalDate, EntryEntity>?
    ) {
        habitServerId?.let {
            val response = habitApi.updateHabitEntries(EntriesModel(entryList!!.values.map { entryMapper.mapToEntryModel(it) }), it)
            if(response.isSuccessful)
                Log.e("TAG", "onResponse: updateHabitEntriesToRemote $response")
            else
                Log.e("TAG", "onFailure: updateHabitEntriesToRemote")
        }
    }

    override suspend fun deleteFromLocal(habitId: String): Int {
        return habitDao.deleteHabit(habitId = habitId)
    }

    override suspend fun deleteGroupHabitFromLocal(habitGroupId: String): Int {
        return habitDao.deleteGroupHabit(habitGroupId = habitGroupId)
    }


    override suspend fun removeMembersFromGroupHabit(
        habitGroupId: String,
        groupHabitServerId: String?,
        userIds: List<String>
    ): Int {
        if (Connectivity.isInternetConnected(context)) {
            removedMembersFromGroupHabitFromRemote(groupHabitServerId, userIds)
        }
        habitDao.updateRemoveGroupHabitDeleteStatus(habitGroupId)
        return habitDao.removeMembersFromGroupHabit(habitGroupId = habitGroupId, userIds = userIds)
    }

    override suspend fun addMembersToGroupHabit(
        groupHabit: GroupHabit?,
        userIds: List<String>
    ): Flow<Boolean> {
        var habits = mutableListOf<HabitEntity>()
        userIds.map { userId ->
            habits.add(
                HabitEntity(
                    UUID.randomUUID().toString(),
                    null,
                    groupHabit?.title,
                    groupHabit?.description,
                    groupHabit?.reminderQuestion,
                    groupHabit?.startDate,
                    groupHabit?.endDate,
                    groupHabit?.isReminderOn,
                    groupHabit?.reminderTime,
                    null,
                    HabitRecordSyncType.ADD_MEMBER_HABIT,
                    groupHabit?.serverId,
                    userId,
                    habitGroupLocalId = groupHabit?.id
                )
            )
        }
        habitDao.addHabit(habits)
        if (Connectivity.isInternetConnected(context)) {
            return addMembersToGroupHabitFromRemote(groupHabit?.serverId, userIds)
        } else {
            return flow { emit(false) }
        }

    }

    override suspend fun getGroupAdminHabit(admin: String?, localId: String): HabitEntity {
        return habitDao.getGroupAdminHabit(admin, localId)
    }

    override suspend fun removedMembersFromGroupHabitFromRemote(
        groupHabitServerId: String?,
        userIds: List<String>
    ) {
        groupHabitServerId?.let { groupHabitId ->
            habitApi.removeMemberFromGroup(groupHabitId, UserIdsModel(userIds))
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) { Log.e("TAG", "onResponse: ${response.body()}") }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Log.e("TAG", "onFailure: removedMembersFromGroupHabitFromRemote ${t.printStackTrace()}",)
                    }
                })
        }

    }


    override suspend fun addMembersToGroupHabitFromRemote(
        groupHabitServerId: String?,
        userIds: List<String>
    ): Flow<Boolean> {
        return flow {
            groupHabitServerId?.let {
                runCatching {
                    val addMemberResponse =
                        habitApi.addMembersToGroup(groupHabitServerId, UserIdsModel(userIds))
                    if (addMemberResponse.isSuccessful) {
                        val getHabitResponse = habitApi.getGroupHabit(groupHabitServerId)
                        if (getHabitResponse.isSuccessful) {
                            val groupHabit = getHabitResponse.body()?.data
                            habitDao.addGroupHabit(listOf(groupHabitMapper.toGroupHabitModel(getHabitResponse.body()?.data!!)))
                            var habits = mutableListOf<HabitEntity>()
                            getHabitResponse.body()?.data?.habits?.map {
                                it?.let { habit ->
                                    habit?.startDate = groupHabit?.startDate
                                    habit?.endDate = groupHabit?.endDate
                                    habit?.description = groupHabit?.description
                                    habit?.title = groupHabit?.title
                                    habit?.habitGroupId = groupHabit?.id
                                    habit?.habitGroupLoacalId = groupHabit?.localId
                                    habits.add(habitMapper.mapToHabitEntityFromHabitModel(habit))
                                }
                            }
                            habitDao.addHabit(habits)
                            emit(true)
                        } else emit(false)
                    } else emit(false)
                }.onFailure {
                    Log.e("add", "addMembersToGroupHabitFromRemote error : ${it.message}")
                    emit(false)
                }

            }
        }

    }


    override suspend fun updateGroupHabitToRemote(groupHabit: GroupHabitsEntity) {
        if (groupHabit.serverId != null) {
            val response = habitApi.updateGroupHabit( groupHabitMapper.toGroupHabitEntity(groupHabit), groupHabit.serverId!!)
            if(response.isSuccessful)
                CoroutineScope(Dispatchers.IO).launch { getGroupHabits(this).collectLatest {  } }
            else
                Log.e("TAG", "updateGroupHabitToRemote: ${Gson().toJson(response.errorBody())}", )
        } else {
            //In case of GroupHabit is Added and updated without internet access (GroupHabit is not added to server due no internet)
            addGroupHabitToRemote(groupHabit)
        }
    }

    //Not Using this function
    override suspend fun getHabitEntries(habitId: String): HashMap<LocalDate, Entry>? {
        val habitEntries = habitDao.getHabitEntries(habitId)?.toMutableMap()
        Log.e("TAG", "getHabitEntries: repo $habitEntries")
        return if (habitEntries != null) {
            HashMap(habitEntries.mapValues { entryMapper.mapToEntry(it.value) }) as HashMap<LocalDate, Entry>?
        } else {
            null
        }
    }

    override suspend fun updateHabitEntries(
        habitServerId: String?,
        habitId: String,
        entries: HashMap<LocalDate, Entry>
    ): Int {
        val mappedEntries = entries.mapValues { entryMapper.mapFromEntry(it.value) }.toMutableMap()
        val res = habitDao.updateHabitEntries(habitId, mappedEntries)
        if (Connectivity.isInternetConnected(context))
            updateHabitEntriesToRemote(habitServerId, mappedEntries)
        return res
    }



}