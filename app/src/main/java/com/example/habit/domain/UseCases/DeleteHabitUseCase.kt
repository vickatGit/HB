package com.example.habit.domain.UseCases

import com.example.habit.data.Repository.HabitRepoImpl
import com.example.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo,
) {
    suspend operator fun invoke(habitServerId:String?, habitId:String?): Int {
        return habitRepo.removeHabit(habitServerId,habitId)
    }
}