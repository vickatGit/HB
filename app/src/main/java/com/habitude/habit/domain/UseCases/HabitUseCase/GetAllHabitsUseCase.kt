package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.HabitThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllHabitsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(coroutineScope: CoroutineScope): List<HabitThumb> {
        return habitRepo.getAllHabitsForProgress(coroutineScope)
    }
}