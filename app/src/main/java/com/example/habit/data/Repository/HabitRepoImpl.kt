package com.example.habit.data.Repository

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.habit.data.Mapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper
import com.example.habit.data.local.HabitDao
import com.example.habit.data.network.HabitApi
import com.example.habit.data.network.model.HabitsListModel.HabitsListModel
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class HabitRepoImpl(
    private val habitDao: HabitDao,
    private val habitMapper: HabitMapper,
    private val entryMapper: EntryMapper,
    private val habitApi:HabitApi,
    private val connectivityManager: ConnectivityManager
) : HabitRepo {
    override suspend fun addHabit(habit: Habit) {
        habitDao.addHabit(listOf(habitMapper.mapToHabitEntity(habit)))
    }

    override suspend fun removeHabit(habitId: String): Int {
        return habitDao.deleteHabit(habitId)
    }

    override fun getHabits(coroutineScope:CoroutineScope): Flow<List<HabitThumb>> {
        if(isInternetConnected()){
            habitApi.getHabits().enqueue(object : Callback<HabitsListModel> {
                override fun onResponse(call: Call<HabitsListModel>, response: Response<HabitsListModel>) {
                    Log.e("TAG", "onResponse: getHabits $response", )
                    if(response.code()==200) {
                        val habitList = response.body()
                        val habits = habitList!!.data.map { habit ->
                            habitMapper.mapToHabitEntityFromHabitModel(habit)
                        }
                        coroutineScope.launch {
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
        return habitDao.updateHabitEntries(
            habitId,
            entries.mapValues {
                entryMapper.mapFromEntry(it.value)
            }.toMutableMap()
        )
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