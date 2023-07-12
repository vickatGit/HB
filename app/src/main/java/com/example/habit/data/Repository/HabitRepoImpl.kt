package com.example.habit.data.Repository

import com.example.habit.data.Mapper.HabitMapper
import com.example.habit.data.local.HabitDao
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepoImpl(val habitDao: HabitDao, val habitMapper: HabitMapper) : HabitRepo {
    override suspend fun addHabit(habit: Habit): Long {
        return habitDao.addHabit(habit = habitMapper.mapToHabitEntity(habit))
    }

    override suspend fun removeHabit(habit: Habit) {
        habitDao.removeHabit(habit = habitMapper.mapToHabitEntity(habit))
    }

    override fun getHabits(): Flow<List<HabitThumb>> {
        return habitDao.getHabits().map { habitEntities ->
            habitEntities.map { habitEntity ->
                habitMapper.mapFromHabitEntity(habitEntity)
            }
        }
    }
}