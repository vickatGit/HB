package com.example.habit.domain.UseCases.HabitUseCase

import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import java.time.LocalDate
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepo
) {
    suspend operator fun invoke(habit: Habit){
        var start = habit.startDate!!
        val entries = hashMapOf<LocalDate,Entry>()
        while (!start.isEqual(habit.endDate!!.plusDays(1))){
            entries[start] = Entry(start,0,false)
            start=start.plusDays(1)
        }
        habit.entries=entries
        habitRepo.addHabit(habit)
    }
}