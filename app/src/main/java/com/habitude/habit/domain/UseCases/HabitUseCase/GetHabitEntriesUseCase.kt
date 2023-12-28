package com.habitude.habit.domain.UseCases.HabitUseCase

import android.util.Log
import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.Entry
import java.sql.Timestamp
import java.time.LocalDate
import javax.inject.Inject

class GetHabitEntriesUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habitId: String):HashMap<LocalDate,Entry>?{
        Log.e("TAG", "invoke: GetHabitEntriesUseCase", )
        return habitRepo.getHabitEntries(habitId = habitId)
    }
}