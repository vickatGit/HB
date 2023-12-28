package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.GroupHabitWithHabits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupHabitFromRemoteUseCase @Inject constructor(
    private val habitRepo: HabitRepo
)  {
    suspend operator fun invoke(groupId:String): Flow<Boolean> {
        return habitRepo.getGroupHabitFromRemote(groupId)
    }
}