package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.GroupHabitWithHabits
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