package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val habitsMapper: HabitMapper
) : ViewModel() {

    private var _habitsState = MutableStateFlow<HabitsUiState>(HabitsUiState.Nothing)
    val habitsUiState = _habitsState.asStateFlow()

    fun getHabits() {
        viewModelScope.launch {
            _habitsState.update { HabitsUiState.Loading }
            try {
                getHabitUseCase().collect { habits ->
                    _habitsState.update {
                        HabitsUiState.HabitsFetched(
                            habits.map {
                                habitsMapper.mapToHabitThumbView(it)
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _habitsState.update { HabitsUiState.Error(e.message) }
            }

        }
    }

}