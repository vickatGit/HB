package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.GroupHabit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddMembersToGroupHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(
        habitGroup: GroupHabit,
        userIds: List<String>,
    ): Flow<Boolean> {
        return habitRepo.addMembersToGroupHabit(habitGroup,userIds)
    }
}