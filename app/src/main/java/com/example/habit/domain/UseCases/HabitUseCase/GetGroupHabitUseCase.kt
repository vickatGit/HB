package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.data.local.entity.HabitGroupWithHabits
import com.example.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class GetGroupHabitUseCase@Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(groupId:String): HabitGroupWithHabits {
        return habitRepo.getGroupHabit(groupId)
    }
}