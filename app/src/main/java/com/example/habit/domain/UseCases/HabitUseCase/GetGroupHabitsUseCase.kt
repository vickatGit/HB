package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.GroupHabitWithHabits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupHabitsUseCase @Inject constructor(
    private val habitRepo: HabitRepo
)  {
    suspend operator fun invoke(coroutineScope: CoroutineScope): Flow<List<GroupHabitWithHabits>> {
        return habitRepo.getGroupHabits(coroutineScope)
    }
}