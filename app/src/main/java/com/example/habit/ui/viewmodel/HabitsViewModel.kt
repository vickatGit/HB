package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.GetCompletedHabitThumbsUseCase
import com.example.habit.domain.UseCases.GetHabitThumbsUseCase
import com.example.habit.ui.fragment.Habits.HabitsUiState
import com.example.habit.ui.mapper.HabitMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
            val statD=System.currentTimeMillis()
            try {
                getHabitUseCase(this).collect { habits ->
                    val endD=System.currentTimeMillis()
                    Log.e("TAG", "getHabits: milli ${endD-statD}", )
                    _habitsState.update {
                        HabitsUiState.HabitsFetched(
                            habits.map {
                                habitsMapper.mapToHabitThumbView(it)
                            }
                        )
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
                        HabitsUiState.HabitsFetched(
                            habits.map {
                                habitsMapper.mapToHabitThumbView(it)
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "getHabits: "+e.printStackTrace() )
                _habitsState.update { HabitsUiState.Error(e.message) }
            }

        }
    }

}