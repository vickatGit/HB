package com.example.habit.data.Repository

import com.example.habit.data.local.HabitDao
import com.example.habit.data.models.Habit
import com.example.habit.domain.Repository.HabitRepo
import kotlinx.coroutines.flow.Flow

class HabitRepoImpl(val habitDao: HabitDao) : HabitRepo {
    override suspend fun addHabit(habit: Habit): Long {
        return habitDao.addHabit(habit = habit)
    }

    override suspend fun removeHabit(habit: Habit) {
        habitDao.removeHabit(habit = habit)
    }

    override fun getHabits(): Flow<List<Habit>> {
        return habitDao.getHabits()
    }
}