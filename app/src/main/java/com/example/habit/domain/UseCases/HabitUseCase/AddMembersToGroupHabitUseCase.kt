package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.GroupHabit
import javax.inject.Inject

class AddMembersToGroupHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(
        habitGroup: GroupHabit,
        userIds: List<String>,
    ) {
        habitRepo.addMembersToGroupHabit(habitGroup,userIds)
    }
}