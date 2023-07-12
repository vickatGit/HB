package com.example.habit.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.AddHabitUseCase
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import com.example.habit.ui.fragment.AddHabit.AddHabitUiState
import com.example.habit.ui.mapper.HabitMapper
import com.example.habit.ui.model.HabitView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val habitMapper : HabitMapper
): ViewModel() {

    private var _uiState= MutableStateFlow<AddHabitUiState>(AddHabitUiState.Error(""))
    val uiState=_uiState.asStateFlow()

    fun addHabit(habit: HabitView, context:Context){
        habit.reminderTime?.let {reminderTime ->
            viewModelScope.launch {
                try {
                    _uiState.update { AddHabitUiState.Loading }
                    val id = addHabitUseCase(habit = habitMapper.mapFromHabit(habit))
                    if (id > 0) {
                        Log.e("TAG", "addHabit: Insertion Success",)
                        scheduleAlarmUseCase(
                            id.toInt(),
                            habit.reminderQuestion!!,
                            reminderTime,
                            context = context
                        )
                        _uiState.update { AddHabitUiState.Success("Habit is Added") }
                    } else {
                        Log.e("TAG", "addHabit: Insertion failed",)
                        _uiState.update { AddHabitUiState.Error("Failed to Add Habit") }
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "addHabit: Insertion failed some error occurred ${e.message}",)
                    _uiState.update { AddHabitUiState.Error(e.message?:"") }
                }


            }
        }
    }
}