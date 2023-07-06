package com.example.habit.domain.UseCases

import com.example.habit.data.models.Habit
import com.example.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habit: Habit): Long {
        return habitRepo.addHabit(habit)
    }
}