package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class DeleteGroupHabitUseCase  @Inject constructor(
    private val habitRepo: HabitRepo
){
    suspend operator fun invoke(groupHabitServerId: String?, groupHabitId:String): Int {
        return habitRepo.removeGroupHabit(groupHabitServerId,groupHabitId)
    }
}