package com.example.habit.ui.fragment.GroupHabit

import com.example.habit.domain.models.Habit
import com.example.habit.ui.model.GroupHabitWithHabitsView

sealed class GroupHabitUiState {
    object Loading : GroupHabitUiState()
    object Nothing : GroupHabitUiState()
    data class Error(val error: String) : GroupHabitUiState()
    data class Habits(val habits: List<Habit>) : GroupHabitUiState()
    data class GroupHabit(val groupHabit: GroupHabitWithHabitsView) : GroupHabitUiState()
}
