package com.habitude.habit.ui.fragment.Habits

import com.habitude.habit.ui.model.HabitThumbView

sealed class HabitsUiState{
    data class HabitsFetched(val habits: List<HabitThumbView>) : HabitsUiState()
    object Loading:HabitsUiState()
    data class Error(val error: String?) : HabitsUiState()
    object Nothing : HabitsUiState()
}
