package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.Habit
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habitId:String): Habit {
        return habitRepo.getHabit(habitId)
    }
}