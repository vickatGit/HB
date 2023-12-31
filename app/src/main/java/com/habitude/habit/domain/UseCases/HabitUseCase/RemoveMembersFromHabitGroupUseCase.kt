package com.habitude.habit.domain.UseCases.HabitUseCase


import com.habitude.habit.domain.Repository.HabitRepo
import javax.inject.Inject

class RemoveMembersFromHabitGroupUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(
        habitGroupId: String,
        userIds: List<String>,
        groupHabitServerId: String?
    ) {
        habitRepo.removeMembersFromGroupHabit(habitGroupId,groupHabitServerId,userIds)
    }
}