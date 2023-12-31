package com.habitude.habit.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.data.local.entity.HabitEntity
import com.habitude.habit.domain.UseCases.HabitUseCase.AddGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.AddHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.DeleteAlarmUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetGroupHabitsUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.UpdateGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.UpdateHabitEntriesUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.UpdateHabitUseCase
import com.habitude.habit.domain.models.Entry
import com.habitude.habit.ui.fragment.AddHabit.AddHabitUiState
import com.habitude.habit.ui.mapper.HabitMapper.EntryMapper
import com.habitude.habit.ui.mapper.HabitMapper.HabitMapper
import com.habitude.habit.ui.model.EntryView
import com.habitude.habit.ui.model.GroupHabitView
import com.habitude.habit.ui.model.HabitView
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val addGroupHabitUseCase: AddGroupHabitUseCase,
    private val habitMapper: HabitMapper,
    private val groupHabitMapper: com.habitude.habit.ui.mapper.GroupHabitMapper.GroupHabitMapper,
    private val updateGroupHabitUseCase: UpdateGroupHabitUseCase
) : ViewModel() {


    private var _uiState = MutableStateFlow<AddHabitUiState>(AddHabitUiState.Error(""))
    val uiState = _uiState.asStateFlow()

    fun addHabit(habit: HabitView, context: Context) {
        habit.reminderTime?.let { reminderTime ->
            viewModelScope.launch {
                try {
                    _uiState.update { AddHabitUiState.Loading }
                    addHabitUseCase(habit = habitMapper.mapFromHabit(habit))
                    Log.e("TAG", "addHabit: Insertion Success")
                    if (habit.isReminderOn!!) {
                        deleteAlarmUseCase(habit.id, context, reminderTime)
                        scheduleAlarmUseCase(
                            habit.id,
                            reminderTime,
                            context = context
                        )
                    } else {
                        deleteAlarmUseCase(habit.id, context, reminderTime)
                    }
                    _uiState.update { AddHabitUiState.Success("Habit is Added") }
                } catch (e: Exception) {
                    Log.e("TAG", "addHabit: Insertion failed some error occurred ${e.message}")
                    _uiState.update { AddHabitUiState.Error(e.message ?: "") }
                }


            }
        }
    }

    fun updateHabit(habit: HabitView, context: Context) {
        habit.reminderTime?.let { reminderTime ->
            viewModelScope.launch {
                try {
                    _uiState.update { AddHabitUiState.Loading }
                    Log.e("TAG", "updateHabit: Insertion Success $habit")
                    updateHabitUseCase(habit = habitMapper.mapFromHabit(habit))
                    if (habit.isReminderOn!!) {
                        deleteAlarmUseCase(habit.id, context, reminderTime)
                        scheduleAlarmUseCase(
                            habit.id,
                            reminderTime,
                            context = context
                        )
                    } else {
                        deleteAlarmUseCase(habit.id, context, reminderTime)
                    }
                    _uiState.update { AddHabitUiState.Success("Habit Updated") }

                } catch (e: Exception) {
                    Log.e("TAG", "addHabit: Insertion failed some error occurred ${e.message}")
                    _uiState.update { AddHabitUiState.Error(e.message ?: "") }
                }
            }
        }
    }



    fun addGroupHabit(habit: HabitView) {
        viewModelScope.launch {
            _uiState.update { AddHabitUiState.Loading }
            try {
                addGroupHabitUseCase(groupHabitMapper.toGroupHabitFromHabit(habit))
                _uiState.update { AddHabitUiState.Success("Group Created Successfully") }
            } catch (e: Exception) {
                Log.e("TAG", "addGroupHabit: ${e.message}")
                _uiState.update { AddHabitUiState.Error(e.message ?: "") }
            }

        }
    }

    fun updateGroupHabit(groupHabitView: GroupHabitView) {
        viewModelScope.launch {
            try {
                updateGroupHabitUseCase(groupHabitMapper.toGroupHabit(groupHabitView))
                _uiState.update { AddHabitUiState.Success("Habit Group Updated") }

            } catch (e: Exception) {
                Log.e("TAG", "updateGroupHabit: ${e.message} ${e.printStackTrace()}",)
                _uiState.update { AddHabitUiState.Error(e.message + "") }
            }
        }
    }

}