package com.example.habit.ui.fragment.Habit

import com.example.habit.ui.model.HabitView

sealed class HabitUiState{
    data class DataFetched(val habit:HabitView) : HabitUiState()
    data class Error(val error: String?) : HabitUiState()
    object Loading : HabitUiState()
    object Nothing : HabitUiState()
}
