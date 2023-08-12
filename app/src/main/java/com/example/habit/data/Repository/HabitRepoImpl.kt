package com.example.habit.data.Repository

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habit.data.Mapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper
import com.example.habit.data.SyncManager
import com.example.habit.data.local.HabitDao
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.network.HabitApi
import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import com.example.habit.data.network.model.UpdateHabitEntriesModel.EntriesModel
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalDate

class HabitRepoImpl(
    private val habitDao: HabitDao,
    private val habitMapper: HabitMapper,
    private val entryMapper: EntryMapper,
    private val habitApi: HabitApi,
    private val connectivityManager: ConnectivityManager,
    private val syncRequest: OneTimeWorkRequest,
    private val workManager: WorkManager
) : HabitRepo {
    override suspend fun addHabit(habit: Habit) {
        habitDao.addHabit(listOf(habitMapper.mapToHabitEntity(habit,HabitRecordSyncType.AddHabit)))
        if(isInternetConnected()){
            addOrUpdateHabitToRemote(habitMapper.mapToHabitEntity(habit,HabitRecordSyncType.AddHabit))
        }
    }
    override suspend fun updateHabit(habit: Habit) {
        val habitEntity=habitMapper.mapToHabitEntity(habit,HabitRecordSyncType.AddHabit)
        habitEntity.habitSyncType=HabitRecordSyncType.UpdateHabit
        habitDao.updateHabit(habitEntity)
        Log.e("TAG", "updateHabit: network ${isInternetConnected()}", )
        if(isInternetConnected()){
            updateHabitToRemote(habitMapper.mapToHabitEntity(habit,HabitRecordSyncType.AddHabit))
        }
    }

    override suspend fun removeHabit(habitServerId:String? , habitId: String?): Int {
        val res = habitDao.updateDeleteStatus(habitId!!)
        if(isInternetConnected()){
            deleteFromRemote(habitId,habitServerId)
        }
        return res
//        return habitDao.deleteHabit(habitId)
    }

    override fun getHabits(coroutineScope:CoroutineScope): Flow<List<HabitThumb>> {
        Log.e("TAG", "onResponse: getHabits local", )
        if(isInternetConnected()){
            habitApi.getHabits().enqueue(object : Callback<HabitsListModel> {
                override fun onResponse(call: Call<HabitsListModel>, response: Response<HabitsListModel>) {
                    if(response.code()==200) {
                        Log.e("TAG", "onResponse: getHabits", )
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
                    Log.e("TAG", "onFailure: ${t.printStackTrace()}", )
                }
            })
        }
        return habitDao.getHabits().map {
            it.map {
                habitMapper.mapFromHabitEntity(it)
            }
        }
    }

    override suspend fun getHabit(habitId: String): Habit {
        return habitDao.getHabit(habitId).let {
            Log.e("TAG", "getHabit: data" + habitId)
            habitMapper.mapToHabit(habitDao.getHabit(habitId))
        }
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

    override fun getUnSyncedHabits(): Flow<List<HabitEntity>> {
        return habitDao.getUnSyncedHabits()
    }

    override suspend fun deleteFromRemote(habitId: String, habitServerId: String?) {
        if(habitServerId!=null) {
            habitApi.deleteHabit(habitServerId).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: deleteFromRemote $response",)
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
                    Log.e("TAG", "onFailure: deleteFromRemote",)
                }
            })
        }else{
            deleteFromLocal(habitId)
        }
    }

    override suspend fun addOrUpdateHabitToRemote(habit: HabitEntity) {
        habitApi.addHabit(habitMapper.mapHabitModelToFromHabitEntity(habit)).enqueue(object :
            Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.e("TAG", "onResponse: addOrUpdateHabitToRemote $response", )
                if(response.code()==200){
//                    getHabits(CoroutineScope(Dispatchers.IO))
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("TAG", "onFailure: addOrUpdateHabitToRemote", )
            }

        })
    }

    override suspend fun updateHabitToRemote(habit: HabitEntity) {
        if(habit.serverId!=null) {
            habitApi.updateHabit(
                habitMapper.mapHabitModelToFromHabitEntity(habit),
                habit.serverId
            ).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.e("TAG", "onResponse: updateHabitToRemote $response",)
                    if (response.code() == 200) {

                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e("TAG", "onFailure: updateHabitToRemote ${t.localizedMessage}",)
                }

            })
        }else{
            addOrUpdateHabitToRemote(habit)
        }
    }

    override suspend fun updateHabitEntriesToRemote(habitId: String,entryList: Map<LocalDate, EntryEntity>?) {
        habitApi.updateHabitEntries(EntriesModel(entryList!!.values.map { entryMapper.mapToEntryModel(it) }),habitId).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.e("TAG", "onResponse: updateHabitEntriesToRemote $response", )
                if(response.code()==200){
//                    getHabits(CoroutineScope(Dispatchers.IO))
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("TAG", "onFailure: updateHabitEntriesToRemote", )
            }

        })
    }

    override suspend fun deleteFromLocal(habitId: String): Int {
        return habitDao.deleteHabit(habitId = habitId)
    }

    //Not Using this function
    override suspend fun getHabitEntries(habitId: String): HashMap<LocalDate, Entry>? {
        val habitEntries = habitDao.getHabitEntries(habitId)?.toMutableMap()
        Log.e("TAG", "getHabitEntries: repo $habitEntries", )
        return if(habitEntries!=null){
            HashMap(habitEntries.mapValues { entryMapper.mapToEntry(it.value) }) as HashMap<LocalDate, Entry>?
        }else {
            null
        }
    }

    override suspend fun updateHabitEntries(habitId: String, entries: HashMap<LocalDate, Entry>): Int {
        Log.e("TAG", "updateHabitEntries: from repo entry src ${Gson().toJson(entries)}", )
        val res = habitDao.updateHabitEntries(
            habitId,
            entries.mapValues {
                entryMapper.mapFromEntry(it.value)
            }.toMutableMap()
        )
        if(isInternetConnected()){
//            workManager.cancelAllWork()
            workManager.enqueueUniqueWork("dds",ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<SyncManager>().apply {
                      
                    setInitialDelay(Duration.ofSeconds(0))

                }.build())
        }
        return res
    }

    private fun isInternetConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
            return actNw?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                    && actNw?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}