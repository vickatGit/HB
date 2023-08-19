package com.example.habit.ui.fragment.AddHabit

import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.local.entity.HabitGroupWithHabits

sealed class AddHabitUiState {
    object Loading:AddHabitUiState()
    object Nothing:AddHabitUiState()
    data class Error(val error:String):AddHabitUiState()
    data class Success(val msg:String):AddHabitUiState()
    data class Habits(val habits :  List<HabitEntity>):AddHabitUiState()
    data class GroupHabit(val groupHabit : HabitGroupWithHabits):AddHabitUiState()

}