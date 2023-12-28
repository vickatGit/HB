package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.data.Repository.HabitRepoImpl
import com.habitude.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo,
) {
    suspend operator fun invoke(habitServerId:String?, habitId:String?): Int {
        return habitRepo.removeHabit(habitServerId,habitId)
    }
}