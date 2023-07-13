package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.GetHabitUseCase
import com.example.habit.ui.fragment.Habit.HabitUiState
import com.example.habit.ui.mapper.HabitMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getHabitUseCase: GetHabitUseCase,
    private val habitMapper: HabitMapper
) : ViewModel() {

    private var _habitUiState= MutableStateFlow<HabitUiState>(HabitUiState.Nothing)
    val habitUiState=_habitUiState.asStateFlow()

    fun getHabit(habitId :String,noHabitError :String){
        viewModelScope.launch {
            _habitUiState.update { HabitUiState.Loading }
            try {
                val habit = getHabitUseCase(habitId.toInt())
                if(habit!=null)
                    _habitUiState.update { HabitUiState.DataFetched(habit = habitMapper.mapToHabit(habit)) }
                else
                    _habitUiState.update { HabitUiState.Error(noHabitError) }
            }catch (e:Exception){
                _habitUiState.update { HabitUiState.Error(e.message) }
            }
        }
    }
}