package com.example.habit.data.Repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.habit.data.Mapper.GroupHabitMapper.GroupHabitMapper
import com.example.habit.data.Mapper.HabitMapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper.HabitMapper
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
        if (isInternetConnected()) {
            addOrUpdateHabitToRemote(
                habitMapper.mapToHabitEntity(
                    habit,
                    HabitRecordSyncType.AddHabit
                )
            )
        }
    }

    override suspend fun addGroupHabit(habit: GroupHabit) {
        val groupHabit = groupHabitMapper.fromGroupHabit(habit, HabitGroupRecordSyncType.AddHabit)
        groupHabit.admin = authPref.getUserId()
        val members = listOf(Member(userId = authPref.getUserId(), username = ""))
        groupHabit.members = Gson().toJson(members)
        Log.e("TAG", "addGroupHabit: data ${Gson().toJson(habit)}")
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
        if (isInternetConnected()) {
            habit.admin=authPref.getUserId()
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
        Log.e("TAG", "updateHabit: network ${isInternetConnected()}")
        if (isInternetConnected()) {
            updateHabitToRemote(habitMapper.mapToHabitEntity(habit, HabitRecordSyncType.AddHabit))
        }
    }

    override suspend fun updateGroupHabit(habit: GroupHabit) {
        val groupHabit =
            groupHabitMapper.fromGroupHabit(habit, HabitGroupRecordSyncType.UpdateHabit)
        Log.e("TAG", "updateGroupHabit: $groupHabit")
        val s = habitDao.updateGroupHabit(
            groupHabit.localId,
            groupHabit.title!!,
            groupHabit.description!!,
            groupHabit.startDate!!,
            groupHabit.endDate!!,
            groupHabit.isReminderOn!!,
            groupHabit.reminderQuestion!!,
            groupHabit.reminderTime!!
        )
        Log.e("TAG", "updateGroupHabit:s :-- ${s}")
        Log.e("TAG", "updateGroupHabit: ${getGroupHabit(groupHabit.localId)}")
        if (isInternetConnected()) {
            updateGroupHabitToRemote(groupHabit)
        }
    }


    override suspend fun removeHabit(habitServerId: String?, habitId: String?): Int {
        val res = habitDao.updateDeleteStatus(habitId!!)
        if (isInternetConnected()) {
            deleteFromRemote(habitId, habitServerId)
        }
        return res
    }

    override suspend fun removeGroupHabit(groupHabitServerId: String?, groupHabitId: String?): Int {
        val res = habitDao.updateGroupHabitDeleteStatus(groupHabitId!!)
        if (isInternetConnected()) {
            deleteGroupHabitFromRemote(groupHabitId, groupHabitServerId)
        }
        return res
    }

    override fun getHabits(coroutineScope: CoroutineScope): Flow<List<HabitThumb>> {
        Log.e("TAG", "onResponse: getHabits local")
        if (isInternetConnected()) {
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
                        coroutineScope.launch {
                            habitDao.deleteAllHabits()
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


        if (isInternetConnected()) {
            getHabits(coroutineScope)
            habitApi.getGroupHabits().enqueue(object : Callback<GroupHabitsModel> {
                override fun onResponse(
                    call: Call<GroupHabitsModel>,
                    response: Response<GroupHabitsModel>
                ) {
                    Log.e("TAG", "onResponse: getGroupHabits $response")
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
                                        it?.habitGroupId = groupHabit.localId
                                        habitMapper.mapToHabitEntityFromHabitModel(it!!)
                                    }
                                    groupHabitMapper.toGroupHabitModel(groupHabit!!)
                                }
                            }
                        }
                        Log.e("TAG", "onResponse vghcv cxgh: $groupHabits")
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.e("TAG", "onResponse vghcv cxgh 2: $groupHabits")
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
                userHabit?.let {
                    groupHabit.habits = listOf(userHabit)
                }

                groupHabitMapper.toGroupHabitWithHabits(groupHabit)
            }
        }

    }

    override suspend fun getHabit(habitId: String): Habit {
        return habitDao.getHabit(habitId).let {
            Log.e("TAG", "getHabit: data" + habitId)
            habitMapper.mapToHabit(habitDao.getHabit(habitId))
        }
    }

    override suspend fun getGroupHabit(groupId: String): GroupHabitWithHabits? {
        val res = habitDao.getGroupHabit(groupId)
        Log.e("TAG", "getGroupHabit: $res")
        return res?.let { groupHabitMapper.toGroupHabitWithHabits(res) }
    }


    override suspend fun getHabitThumb(habitId: String): Habit {
        return habitDao.getHabit(habitId).let {
            Log.e("TAG", "getHabit: data" + habitId)
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
        Log.e("grp", "addOrUpdateHabitToRemote: $habit ", )
        return flow {
            val response = habitApi.addHabit(habitMapper.mapHabitModelToFromHabitEntity(habit))
            Log.e("grp", "addOrUpdateHabitToRemote: $response", )
            if (response.isSuccessful) {
                emit(response.body())
                Log.e("grp", "addOrUpdateHabitToRemote: response ${response.body()}", )
            }
            else {
                emit(response.body())
            }
        }
    }
    override suspend fun addGroupHabitToRemote(habitEntity: GroupHabitsEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            val groupHabit = getGroupAdminHabit(habitEntity.admin, habitEntity.localId)
            Log.e("grp", "addGroupHabitsToRemote: $groupHabit habitId ${habitEntity.localId} admin ${habitEntity.admin}", )
            addOrUpdateHabitToRemote(groupHabit).collect {
                it?.let {
                Log.e("grp", "addGroupHabitToRemote: $it ", )
                    habitApi.addGroupHabit(
                        habit = groupHabitMapper.toGroupHabitEntity(habitEntity),
                        it.habitId
                    ).enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {}
                        override fun onFailure(call: Call<Any>, t: Throwable) {} })

                    getGroupHabits(this)
                }
            }
        }
    }

    override suspend fun updateHabitToRemote(habit: HabitEntity) {
        if (habit.serverId != null) {
            habitApi.updateHabit(

                habitMapper.mapHabitModelToFromHabitEntity(habit),
                habit.serverId
            ).enqueue(object : Callback<Any> {
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
            addOrUpdateHabitToRemote(habit)
        }
    }


    override suspend fun updateHabitEntriesToRemote(
        habitServerId: String?,
        entryList: Map<LocalDate, EntryEntity>?
    ) {
        habitServerId?.let {
            habitApi.updateHabitEntries(
                EntriesModel(entryList!!.values.map { entryMapper.mapToEntryModel(it) }),
                it
            ).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: updateHabitEntriesToRemote $response")
                    if (response.code() == 200) {
                        //                    getHabits(CoroutineScope(Dispatchers.IO))
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("TAG", "onFailure: updateHabitEntriesToRemote")
                }

            })
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
        if (isInternetConnected()) {
            removedMembersFromGroupHabitFromRemote(groupHabitServerId, userIds)
        }
        val s =habitDao.updateRemoveGroupHabitDeleteStatus(habitGroupId)
        Log.e("TAG", "removeMembersFromGroupHabit: $s", )
        return habitDao.removeMembersFromGroupHabit(habitGroupId = habitGroupId, userIds = userIds)
    }

    override suspend fun addMembersToGroupHabit(
        groupHabit: GroupHabit?,
        userIds: List<String>
    ) {
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
                    groupHabit?.id,
                    userId
                )
            )
        }
        habitDao.addHabit(habits)
        if (isInternetConnected()) {
            addMembersToGroupHabitFromRemote(groupHabit?.serverId, userIds)
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
    ) {
        groupHabitServerId?.let {
            habitApi.addMembersToGroup(
                groupHabitServerId,
                UserIdsModel(userIds)
            ).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: addMembersFromGroupHabitFromRemote $response")
                    if (response.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            getGroupHabits(this)
                        }
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("TAG", "onFailure: addMembersFromGroupHabitFromRemote ${t.message}")
                }

            })
        }
    }




    override suspend fun updateGroupHabitToRemote(groupHabit: GroupHabitsEntity) {
        if(groupHabit.serverId!=null) {
            habitApi.updateGroupHabit(
                groupHabitMapper.toGroupHabitEntity(groupHabit),
                groupHabit.serverId!!
            ).enqueue(
                object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        Log.e("TAG", "updateGroupHabitToRemote onResponse: $response")
                        if (response.isSuccessful) {
                            CoroutineScope(Dispatchers.IO).launch {
                                getGroupHabits(this)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Log.e("TAG", "updateGroupHabitToRemote onFailure: ${t.printStackTrace()}")
                    }

                })
        }else{
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
        Log.e("TAG", "updateHabitEntries: from repo entry src ${Gson().toJson(entries)}")
        val res = habitDao.updateHabitEntries(
            habitId,
            entries.mapValues {
                entryMapper.mapFromEntry(it.value)
            }.toMutableMap()
        )
        if (isInternetConnected()) {
            updateHabitEntriesToRemote(
                habitServerId,
                entries.mapValues { entryMapper.mapFromEntry(it.value) })
        }
        return res
    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo != null && activeNetworkInfo.isConnected

    }
}