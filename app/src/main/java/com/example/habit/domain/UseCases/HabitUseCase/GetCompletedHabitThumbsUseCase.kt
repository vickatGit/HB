package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompletedHabitThumbsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    operator fun invoke(): Flow<List<HabitThumb>> {
        return habitRepo.getCompletedHabits()
    }
}