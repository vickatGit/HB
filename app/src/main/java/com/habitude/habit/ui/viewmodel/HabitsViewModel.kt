package com.habitude.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.domain.UseCases.HabitUseCase.GetCompletedHabitThumbsUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.habitude.habit.ui.fragment.Habits.HabitsUiState
import com.habitude.habit.ui.mapper.HabitMapper.HabitMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val getHabitUseCase: GetHabitThumbsUseCase,
    private val getCompletedHabitThumbsUseCase: GetCompletedHabitThumbsUseCase,
    private val habitsMapper: HabitMapper
) : ViewModel() {

    private var _habitsState = MutableStateFlow<HabitsUiState>(HabitsUiState.Nothing)
    val habitsUiState = _habitsState.asStateFlow()

    fun getHabits() {
        viewModelScope.launch {
            _habitsState.update { HabitsUiState.Loading }
            try {
                getHabitUseCase(this).collect { habits ->
                    _habitsState.update {
                        HabitsUiState.HabitsFetched(habits.map { habitsMapper.mapToHabitThumbView(it) })
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "getHabits: "+e.printStackTrace() )
                _habitsState.update { HabitsUiState.Error(e.message) }
            }

        }
    }

    fun getCompletedHabits() {
        viewModelScope.launch {
            _habitsState.update { HabitsUiState.Loading }
            try {
                getCompletedHabitThumbsUseCase().collect { habits ->
                    _habitsState.update {
                        HabitsUiState.HabitsFetched(habits.map { habitsMapper.mapToHabitThumbView(it) })
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "getHabits: "+e.printStackTrace() )
                _habitsState.update { HabitsUiState.Error(e.message) }
            }

        }
    }

}