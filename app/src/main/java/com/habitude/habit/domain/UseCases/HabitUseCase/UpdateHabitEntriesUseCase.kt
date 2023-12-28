package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.Entry
import java.sql.Timestamp
import java.time.LocalDate
import javax.inject.Inject

class UpdateHabitEntriesUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habitServerId:String?, habitId:String,entries:HashMap<LocalDate,Entry>):Int{
        return habitRepo.updateHabitEntries(habitServerId = habitServerId, habitId = habitId, entries = entries)
    }
}