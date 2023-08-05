package com.example.habit.data.Repository

import android.util.Log
import com.example.habit.data.Mapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper
import com.example.habit.data.local.HabitDao
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepoImpl(
    val habitDao: HabitDao,
    val habitMapper: HabitMapper,
    val entryMapper: EntryMapper
) : HabitRepo {
    override suspend fun addHabit(habit: Habit): Long {
        return habitDao.addHabit(habit = habitMapper.mapToHabitEntity(habit))
    }

    override suspend fun removeHabit(habitId: Int): Int {
        return habitDao.deleteHabit(habitId!!)
    }

    override fun getHabits(): Flow<List<HabitThumb>> {
        return habitDao.getHabits().map {
            it.map {
                habitMapper.mapFromHabitEntity(it)
            }
        }
    }

    override suspend fun getHabit(habitId: Int): Habit {
        return habitDao.getHabit(habitId).let {
            Log.e("TAG", "getHabit: data" + habitId)
            habitMapper.mapToHabit(habitDao.getHabit(habitId))
        }
    }

    override suspend fun getHabitThumb(habitId: Int): Habit {
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

    override suspend fun getHabitEntries(habitId: Int): HashMap<LocalDate, Entry>? {
        val habitEntries = habitDao.getHabitEntries(habitId)?.toMutableMap()
        Log.e("TAG", "getHabitEntries: repo $habitEntries", )
        return if(habitEntries!=null){
            HashMap(habitEntries.mapValues { entryMapper.mapToEntry(it.value) }) as HashMap<LocalDate, Entry>?
        }else {
            null
        }
    }

    override suspend fun updateHabitEntries(habitId: Int, entries: HashMap<LocalDate, Entry>): Int {
        Log.e("TAG", "updateHabitEntries: from repo entry src ${Gson().toJson(entries)}", )

        return habitDao.updateHabitEntries(
            habitId,
            entries.mapValues {
                entryMapper.mapFromEntry(it.value)
            }.toMutableMap()
        )
    }
}