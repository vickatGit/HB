package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.GroupHabitWithHabits
import javax.inject.Inject

class GetGroupHabitUseCase@Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(groupId:String): GroupHabitWithHabits? {
        return habitRepo.getGroupHabit(groupId)
    }
}