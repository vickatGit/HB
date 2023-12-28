package com.habitude.habit.ui.fragment.AddHabit

import com.habitude.habit.domain.models.Habit
import com.habitude.habit.ui.model.GroupHabitWithHabitsView

sealed class AddHabitUiState {
    object Loading:AddHabitUiState()
    object Nothing:AddHabitUiState()
    data class Error(val error:String):AddHabitUiState()
    data class Success(val msg:String):AddHabitUiState()
    data class Habits(val habits: List<Habit>):AddHabitUiState()
    data class GroupHabit(val groupHabit: GroupHabitWithHabitsView):AddHabitUiState()

}