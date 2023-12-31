package com.habitude.habit.data.Repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.habitude.habit.data.Mapper.GroupHabitMapper.GroupHabitMapper
import com.habitude.habit.data.Mapper.HabitMapper.EntryMapper
import com.habitude.habit.data.Mapper.HabitMapper.HabitMapper
import com.habitude.habit.data.common.Connectivity
import com.habitude.habit.data.local.HabitDao
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.data.local.entity.EntryEntity
import com.habitude.habit.data.local.entity.GroupHabitsEntity
import com.habitude.habit.data.local.entity.HabitEntity
import com.habitude.habit.data.network.HabitApi
import com.habitude.habit.data.network.model.AddHabitResponseModel.AddHabitResponseModel
import com.habitude.habit.data.network.model.GroupHabitModel.GroupHabitsModel
import com.habitude.habit.data.network.model.HabitsListModel.HabitsListModel
import com.habitude.habit.data.network.model.UpdateHabitEntriesModel.EntriesModel
import com.habitude.habit.data.network.model.UserIdsModel.UserIdsModel
import com.habitude.habit.data.util.HabitGroupRecordSyncType
import com.habitude.habit.data.util.HabitRecordSyncType
import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import com.habitude.habit.domain.models.Entry
import com.habitude.habit.domain.models.GroupHabit
import com.habitude.habit.domain.models.GroupHabitWithHabits
import com.habitude.habit.domain.models.Habit
import com.habitude.habit.domain.models.HabitThumb
import com.habitude.habit.domain.models.Member
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
    private val context: Context,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase
) : HabitRepo {
    override suspend fun addHabit(habit: Habit) {
        habitDao.addHabit(listOf(habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)))
        if (Connectivity.isInternetConnected(context))
            addOrUpdateHabitToRemote(
                habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)
            ).collectLatest { Log.e("TAG", "addHabit: Added to server successfully $it") }
        authPref.setHabitsModified(true)
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
        authPref.setGroupHabitsModified(true)
    }


    override suspend fun updateHabit(habit: Habit) {
        val habitEntity = habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit)
        habitEntity.habitSyncType = HabitRecordSyncType.UpdateHabit
        habitDao.updateHabit(habitEntity)
        if (Connectivity.isInternetConnected(context))
            updateHabitToRemote(habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit))
        authPref.setHabitsModified(true)
    }

    override suspend fun updateGroupHabit(habit: GroupHabit) {
        val groupHabit =
            groupHabitMapper.fromGroupHabit(habit, HabitGroupRecordSyncType.UpdateHabit)
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
        authPref.setGroupHabitsModified(true)
    }


    override suspend fun removeHabit(habitServerId: String?, habitId: String?): Int {
        val res = habitDao.updateDeleteStatus(habitId!!)

        if (Connectivity.isInternetConnected(context)) {
            deleteFromRemote(habitId, habitServerId)
        }
        return res
        authPref.setHabitsModified(true)
    }

    override suspend fun removeGroupHabit(groupHabitServerId: String?, groupHabitId: String?): Int {
        val res = habitDao.updateGroupHabitDeleteStatus(groupHabitId!!)
        Log.e("TAG", "removeHabit: $groupHabitId")
        if (Connectivity.isInternetConnected(context)) {
            deleteGroupHabitFromRemote(groupHabitId, groupHabitServerId)
        }
        return res
        authPref.setGroupHabitsModified(true)
    }



    override fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>> {
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
                            habitDao.deleteAllHabits()
                            habitDao.addHabit(habits)
                        }
                        authPref.setHabitsModified(false)
                    }
                }

                override fun onFailure(call: Call<HabitsListModel>, t: Throwable) {
                    Log.e("TAG", "onFailure: ${t.printStackTrace()}")
                }
            })
        }
        return habitDao.getHabits().map {
            it.filter { habit ->
                (LocalDate.now().isBefore(habit.endDate!!) || habit.endDate!!.isEqual(LocalDate.now()))
            }
        }.map {
            it.map {
                habitMapper.mapFromHabitEntity(it)
            }
        }
    }

    override suspend fun getHabitsForProgress(coroutineScope: CoroutineScope) {
        if (Connectivity.isInternetConnected(context)) {
            val response = habitApi.getHabitsR()
            if (response.isSuccessful) {
                Log.e("TAG", "onResponse: getHabits")
                val habitList = response.body()
                val habits = habitList!!.data.map { habit ->
                    habitMapper.mapToHabitEntityFromHabitModel(habit)
                }
                Log.e("TAG", "onResponse: habits get $habits")
                habitDao.deleteAllHabits()
                habitDao.addHabit(habits)
                authPref.setHabitsModified(false)
            }
        }
    }


    override suspend fun getAllHabitsForProgress(coroutineScope: CoroutineScope): List<HabitThumb> {

        val habits = mutableListOf<HabitThumb>()
        if (authPref.getHabitsModified()) {
            getHabitsForProgress(coroutineScope)
            getGroupHabitsForProgres(coroutineScope)
        }
        habitDao.getHabitsForProgress(userId = authPref.getUserId()).forEach {
            habits.add(habitMapper.mapFromHabitEntity(it))
        }
        habits.forEach { habit ->
            habit.reminderTime?.let {
                scheduleAlarmUseCase(habit.id, it, context)
            }
        }
        Log.e("TAG", "getAllHabitsForProgress: ${Gson().toJson(habits)}", )
        return habits


    }


    override suspend fun getGroupHabits(coroutineScope: CoroutineScope): Flow<List<GroupHabitWithHabits>> {


        if (Connectivity.isInternetConnected(context)) {
            habitDao.deleteAllGroupHabits()
//            getHabits(coroutineScope).collectLatest { }
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
                        authPref.setGroupHabitsModified(false)
                    }
                }

                override fun onFailure(call: Call<GroupHabitsModel>, t: Throwable) {
                    Log.e("TAG", "onFailure: getGroupHabits ${t.stackTrace}")
                }

            })
        }
        return habitDao.getGroupHabits().map { groupHabits ->
            groupHabits.filter { groupHabit ->
                LocalDate.now().isBefore(groupHabit.habitGroup.endDate) || LocalDate.now().isEqual(groupHabit.habitGroup.endDate)
            }
        }.map { groupHabits ->
            groupHabits.map { groupHabit ->
                val userHabit = groupHabit.habits.find { it.userId == authPref.getUserId() }
                userHabit?.let { groupHabit.habits = listOf(userHabit) }
                groupHabitMapper.toGroupHabitWithHabits(groupHabit)
            }
        }

    }

    override suspend fun getGroupHabitsForProgres(coroutineScope: CoroutineScope) {


        if (Connectivity.isInternetConnected(context)) {
            habitDao.deleteAllGroupHabits()
            val response = habitApi.getGroupHabitsR()
            Log.e("TAG", "getHabitsForProgress: ${response}")
            if (response.isSuccessful) {
                response.body()?.let {
                    var habitsList: List<HabitEntity> = listOf()
                    val groupHabits = response.body()?.let {
                        authPref.setGroupHabitsModified(false)
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
                    habitDao.deleteAllGroupHabits()
                    groupHabits?.let { habitDao.addGroupHabit(groupHabits) }
                    habitDao.addHabit(habitsList)
                    authPref.setGroupHabitsModified(false)
                }
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
        Log.e("TAG", "getGroupHabit: repo $res")
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
        return habitDao.getCompletedHabits().map {
            it.filter {
                LocalDate.now().isAfter(it.endDate)
            }
        }.map {
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
                    habitApi.addGroupHabit(
                        groupHabitMapper.toGroupHabitEntity(habitEntity),
                        it.habitId
                    )
                        .enqueue(object : Callback<Any> {
                            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                if (response.isSuccessful)
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
            val response = habitApi.updateHabitEntries(EntriesModel(entryList!!.values.map {
                entryMapper.mapToEntryModel(it)
            }), it)
            if (response.isSuccessful) {
                Log.e("TAG", "onResponse: updateHabitEntriesToRemote $response")
                habitDao.updateHabitEntriesState(habitServerId,HabitRecordSyncType.SyncedHabit)
            }
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
        authPref.setGroupHabitsModified(true)
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
        authPref.setGroupHabitsModified(true)
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
                        if (response.isSuccessful) {
                            Log.e("TAG", "onResponse: ${response.body()}")
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Log.e(
                            "TAG",
                            "onFailure: removedMembersFromGroupHabitFromRemote ${t.printStackTrace()}",
                        )
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
                            habitDao.addGroupHabit(
                                listOf(
                                    groupHabitMapper.toGroupHabitModel(
                                        getHabitResponse.body()?.data!!
                                    )
                                )
                            )
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
            val response = habitApi.updateGroupHabit(
                groupHabitMapper.toGroupHabitEntity(groupHabit),
                groupHabit.serverId!!
            )
            if (response.isSuccessful)
                CoroutineScope(Dispatchers.IO).launch { getGroupHabits(this).collectLatest { } }
            else
                Log.e("TAG", "updateGroupHabitToRemote: ${Gson().toJson(response.errorBody())}")
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
        authPref.setHabitsModified(true)
        if (Connectivity.isInternetConnected(context))
            updateHabitEntriesToRemote(habitServerId, mappedEntries)
        return res
    }


}