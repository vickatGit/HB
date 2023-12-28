package com.habitude.habit.ui.fragment.Habit

import com.habitude.habit.ui.model.EntryView
import com.habitude.habit.ui.model.HabitView
import java.sql.Timestamp
import java.time.LocalDate

sealed class HabitUiState{
    data class HabitData(val habit:HabitView) : HabitUiState()
    data class Error(val error: String?) : HabitUiState()
    data class HabitDeleted(val msg: String?) : HabitUiState()
    data class HabitEntries(val habitEntries : HashMap<LocalDate,EntryView>):HabitUiState()
    object Loading : HabitUiState()
    object Nothing : HabitUiState()
}
