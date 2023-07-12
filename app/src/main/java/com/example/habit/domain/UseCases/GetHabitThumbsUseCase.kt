package com.example.habit.domain.UseCases

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.HabitThumb
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitThumbsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    operator fun invoke(): Flow<List<HabitThumb>> {
        return habitRepo.getHabits()
    }
}