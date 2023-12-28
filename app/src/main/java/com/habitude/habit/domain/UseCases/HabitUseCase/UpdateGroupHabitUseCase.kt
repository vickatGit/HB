package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.GroupHabit
import javax.inject.Inject

class UpdateGroupHabitUseCase  @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habit:GroupHabit){
        habitRepo.updateGroupHabit(habit)
    }
}