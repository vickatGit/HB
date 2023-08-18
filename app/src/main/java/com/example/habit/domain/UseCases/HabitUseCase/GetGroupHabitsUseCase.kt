package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.data.local.entity.HabitGroupWithHabits
import com.example.habit.domain.Repository.HabitRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupHabitsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
)  {
    suspend operator fun invoke(coroutineScope: CoroutineScope): Flow<List<HabitGroupWithHabits>> {
        return habitRepo.getGroupHabits(coroutineScope)
    }
}