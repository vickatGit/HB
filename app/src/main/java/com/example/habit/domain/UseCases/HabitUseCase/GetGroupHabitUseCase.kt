package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.GroupHabitWithHabits
import javax.inject.Inject

class GetGroupHabitUseCase@Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(groupId:String): GroupHabitWithHabits? {
        return habitRepo.getGroupHabit(groupId)
    }
}