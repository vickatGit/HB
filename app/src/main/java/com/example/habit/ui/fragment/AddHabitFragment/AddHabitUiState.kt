package com.example.habit.ui.fragment.AddHabitFragment

sealed class AddHabitUiState {
    object Loading:AddHabitUiState()
    data class Error(val error:String):AddHabitUiState()
    data class Success(val msg:String):AddHabitUiState()
}