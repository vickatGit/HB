package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.HabitThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitThumbsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    operator fun invoke(coroutineScope: CoroutineScope): Flow<List<HabitThumb>> {
        return habitRepo.getHabits(coroutineScope)
    }
}