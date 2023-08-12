package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.DeleteHabitUseCase
import com.example.habit.domain.UseCases.GetHabitEntriesUseCase
import com.example.habit.domain.UseCases.GetHabitUseCase
import com.example.habit.domain.UseCases.UpdateHabitEntriesUseCase
import com.example.habit.domain.models.Entry
import com.example.habit.ui.fragment.Habit.HabitUiState
import com.example.habit.ui.fragment.Habits.HabitsUiState
import com.example.habit.ui.mapper.EntryMapper
import com.example.habit.ui.mapper.HabitMapper
import com.example.habit.ui.model.EntryView
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
class HabitViewModel @Inject constructor(
    private val getHabitUseCase: GetHabitUseCase,
    private val habitMapper: HabitMapper,
    private val entityMapper: EntryMapper,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val updateHabitEntriesUseCase: UpdateHabitEntriesUseCase
) : ViewModel() {

    private var _currentViewingMonth: YearMonth?=null
    fun getCurrentViewingMonth() = _currentViewingMonth
    fun setCurrentViewingMonth(currentViewingMonth:YearMonth) {
        this._currentViewingMonth=currentViewingMonth
    }

    private var _habitUiState= MutableStateFlow<HabitUiState>(HabitUiState.Nothing)
    val habitUiState=_habitUiState.asStateFlow()

    fun getHabit(habitId :String,noHabitError :String){
        viewModelScope.launch {
            _habitUiState.update { HabitUiState.Loading }
            try {
                val habit = getHabitUseCase(habitId)
                if(habit!=null)
                    _habitUiState.update { HabitUiState.HabitData(habit = habitMapper.mapToHabit(habit)) }
                else
                    _habitUiState.update { HabitUiState.Error(noHabitError) }
            }catch (e:Exception){
                _habitUiState.update { HabitUiState.Error(e.message) }
            }
        }
    }

    fun updateHabitEntries(habitId: String,entries : HashMap<LocalDate,EntryView>){
        viewModelScope.launch {
            _habitUiState.update { HabitUiState.Loading }
            try {
                var habitEntries=0
                try {
                    val h = entries.mapValues {
                        entityMapper.mapToEntry(it.value)
                    }
                    Log.e("TAG", "updateHabitEntries: list ${Gson().toJson(entries)}", )
                    habitEntries = updateHabitEntriesUseCase(
                        habitId = habitId,
                        entries = h.toMutableMap() as HashMap<LocalDate, Entry>
                    )
                }catch (e:Exception){
                    Log.e("TAG", "updateHabitEntries: due to update entries ${e.printStackTrace()}", )
                }
                if(habitEntries>0) getHabit(habitId.toString(),"") else _habitUiState.update { HabitUiState.Nothing }

            }catch (e:Exception){
                Log.e("TAG", "updateHabitEntries: due to get entries $e", )
                _habitUiState.update { HabitUiState.Error(e.message) }
            }

        }
    }

    fun deleteHabit(habitServerId:String?, habitId:String?,successMsg:String,errorMsg:String){
        viewModelScope.launch {
            _habitUiState.update { HabitUiState.Loading }
            try {
                deleteHabitUseCase(habitServerId,habitId)
                _habitUiState.update { HabitUiState.HabitDeleted(successMsg) }
            }catch (e:java.lang.Exception){
                _habitUiState.update { HabitUiState.Error(e.message) }
            }
        }
    }


    /*-------------- This Function Not Working Properly--------------

            private fun getHabitEntries(habitId: Int){
                viewModelScope.launch {
                    _habitUiState.update { HabitUiState.Loading }
                    try {
                        val habits = getHabitEntriesUseCase(habitId)?.mapValues {
                            entityMapper.mapFromEntry(it.value)
                        } as HashMap<LocalDate,EntryView>
                        _habitUiState.update { HabitUiState.HabitEntries(habits) }
                    }catch (e:Exception){
                        Log.e("TAG", "getHabitEntries: ${e.printStackTrace()}", )
                        _habitUiState.update { HabitUiState.Error(e.message) }
                    }
                }
            }

     */

}