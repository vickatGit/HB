package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitThumbUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke( habitId : String): Habit {
        return habitRepo.getHabitThumb(habitId)
    }
}