package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.GroupHabit
import javax.inject.Inject

class UpdateGroupHabitUseCase  @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habit:GroupHabit){
        habitRepo.updateGroupHabit(habit)
    }
}