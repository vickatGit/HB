package com.example.habit.domain.UseCases

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Habit
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habitId:Int): Habit {
        return habitRepo.getHabit(habitId)
    }
}