package com.example.habit.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.AddHabitUseCase
import com.example.habit.domain.UseCases.DeleteAlarmUseCase
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import com.example.habit.domain.UseCases.UpdateHabitUseCase
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
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val habitMapper : HabitMapper
): ViewModel() {

    private var _uiState= MutableStateFlow<AddHabitUiState>(AddHabitUiState.Error(""))
    val uiState=_uiState.asStateFlow()

    fun addHabit(habit: HabitView, context:Context){
        habit.reminderTime?.let {reminderTime ->
            viewModelScope.launch {
                try {
                    _uiState.update { AddHabitUiState.Loading }
                    addHabitUseCase(habit = habitMapper.mapFromHabit(habit))
//                    Log.e("TAG", "addHabit: $id", )
//                    if (id > 0) {
                        Log.e("TAG", "addHabit: Insertion Success",)
//                        if(habit.isReminderOn!!) {
//                            deleteAlarmUseCase(id.toInt(),context,reminderTime)
//                            scheduleAlarmUseCase(
//                                id.toInt(),
//                                reminderTime,
//                                context = context
//                            )
//                        }else{
//                            deleteAlarmUseCase(id.toInt(),context,reminderTime)
//                        }
                        _uiState.update { AddHabitUiState.Success("Habit is Added") }
//                    } else {
//                        Log.e("TAG", "addHabit: Insertion failed",)
//                        _uiState.update { AddHabitUiState.Error("Failed to Add Habit") }
//                    }
                } catch (e: Exception) {
                    Log.e("TAG", "addHabit: Insertion failed some error occurred ${e.message}",)
                    _uiState.update { AddHabitUiState.Error(e.message?:"") }
                }


            }
        }
    }
    fun updateHabit(habit: HabitView, context:Context){
        habit.reminderTime?.let {reminderTime ->
            viewModelScope.launch {
                try {
                    _uiState.update { AddHabitUiState.Loading }
                    updateHabitUseCase(habit = habitMapper.mapFromHabit(habit))
//                    Log.e("TAG", "addHabit: $id", )
//                    if (id > 0) {
                    Log.e("TAG", "addHabit: Insertion Success",)
//                        if(habit.isReminderOn!!) {
//                            deleteAlarmUseCase(id.toInt(),context,reminderTime)
//                            scheduleAlarmUseCase(
//                                id.toInt(),
//                                reminderTime,
//                                context = context
//                            )
//                        }else{
//                            deleteAlarmUseCase(id.toInt(),context,reminderTime)
//                        }
                    _uiState.update { AddHabitUiState.Success("Habit is Added") }
//                    } else {
//                        Log.e("TAG", "addHabit: Insertion failed",)
//                        _uiState.update { AddHabitUiState.Error("Failed to Add Habit") }
//                    }
                } catch (e: Exception) {
                    Log.e("TAG", "addHabit: Insertion failed some error occurred ${e.message}",)
                    _uiState.update { AddHabitUiState.Error(e.message?:"") }
                }


            }
        }
    }
}