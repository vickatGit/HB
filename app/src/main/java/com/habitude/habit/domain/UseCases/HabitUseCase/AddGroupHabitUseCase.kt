package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.GroupHabit
import javax.inject.Inject

class AddGroupHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(groupHabit: GroupHabit){
        return habitRepo.addGroupHabit(groupHabit)
    }
}