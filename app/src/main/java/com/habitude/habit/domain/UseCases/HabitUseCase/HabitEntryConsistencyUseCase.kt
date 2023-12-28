package com.habitude.habit.domain.UseCases.HabitUseCase

import com.habitude.habit.domain.Repository.HabitRepo
import com.habitude.habit.domain.models.Entry
import com.habitude.habit.ui.model.EntryView
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate
import javax.inject.Inject

class HabitEntryConsistencyUseCase @Inject constructor(
    private val habitRepo: HabitRepo,
    private val updateHabitEntriesUseCase: UpdateHabitEntriesUseCase
) {
    suspend operator fun invoke(scope:CoroutineScope){
        habitRepo.getAllHabitsForProgress(scope).forEach {habit ->
            habit.entries?.let { entries ->
                if(!entries.contains(LocalDate.now().minusDays(1)))
                    updateEntries(LocalDate.now().minusDays(1),false,entries,habit.id)
                if(!entries.contains(LocalDate.now()))
                    updateEntries(LocalDate.now(),false,entries,habit.id)
            }
            if(habit.entries==null){
                val entries = hashMapOf<LocalDate,Entry>()
                updateEntries(LocalDate.now().minusDays(1),false, entries ,habit.id)
                updateEntries(LocalDate.now(),false,entries,habit.id)
            }

        }
    }
    private suspend fun updateEntries(date: LocalDate, isUpgrade: Boolean, habitEntries: HashMap<LocalDate, Entry>,habitId:String) {
        val prevEntry = habitEntries[date.minusDays(1)]

        if (prevEntry == null) {
            habitEntries[date] = Entry(date, if (isUpgrade) 1 else 0, isUpgrade)
        }
        val habitList= mutableListOf<Entry>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }

        habitList.forEachIndexed {index,it ->
            if (index>0 && (it.timestamp!!.isAfter(date) || it.timestamp.isEqual(date))) {
                var score=habitList.get(if(index!=0) index-1 else index).score
                habitList[index].score=if (isUpgrade) score!!+1 else it.score!!-1
            }
        }
        habitEntries.putAll(habitList.associateBy { it.timestamp!! })
        habitId?.let {
            updateHabitEntriesUseCase(
                habitServerId = null,
                habitId = habitId,
                entries = habitEntries
            )
        }
    }
}