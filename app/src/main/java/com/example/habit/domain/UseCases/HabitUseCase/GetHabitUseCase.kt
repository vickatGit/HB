package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Habit
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habitId:String): Habit {
        return habitRepo.getHabit(habitId)
    }
}