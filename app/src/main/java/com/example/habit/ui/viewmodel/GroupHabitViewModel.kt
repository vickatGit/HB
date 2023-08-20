package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.HabitUseCase.GetGroupHabitUseCase
import com.example.habit.domain.UseCases.HabitUseCase.GetGroupHabitsUseCase
import com.example.habit.domain.UseCases.HabitUseCase.UpdateGroupHabitUseCase
import com.example.habit.domain.UseCases.HabitUseCase.UpdateHabitEntriesUseCase
import com.example.habit.domain.models.Entry
import com.example.habit.ui.fragment.GroupHabit.GroupHabitUiState
import com.example.habit.ui.mapper.HabitMapper.EntryMapper
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
class GroupHabitViewModel @Inject constructor(
    private val getGroupHabitsUseCase: GetGroupHabitsUseCase,
    private val getGroupHabitUseCase: GetGroupHabitUseCase,
    private val updateHabitEntriesUseCase: UpdateHabitEntriesUseCase,
    private val entryMapper: EntryMapper,
    private val groupHabitMapper: com.example.habit.ui.mapper.GroupHabitMapper.GroupHabitMapper
) : ViewModel() {

    private var _uiState = MutableStateFlow<GroupHabitUiState>(GroupHabitUiState.Error(""))
    val uiState = _uiState.asStateFlow()

    private var _currentViewingMonth: YearMonth?=null
    fun getCurrentViewingMonth() = _currentViewingMonth
    fun setCurrentViewingMonth(currentViewingMonth: YearMonth) {
        this._currentViewingMonth=currentViewingMonth
    }

    private var userPosition=0
    fun getHabitStatePos(): Int = userPosition
    fun setHabitStatePos(pos:Int){
        userPosition=pos
    }


    fun getGroupHabits() {
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                getGroupHabitsUseCase(this).collect {grpHabits ->
                    grpHabits.forEach { habits ->
                        habits.habits.forEach {
                            Log.e("TAG", "getGroupHabits: res $it")
                        }
                    }
                    _uiState.update { GroupHabitUiState.Habits(grpHabits) }
                }
            } catch (e: Exception) {
                Log.e("TAG", "getGroupHabits: error ${e.stackTrace}")
                _uiState.update { GroupHabitUiState.Error(e.message+"") }
            }

        }
    }
    fun getGroupHabit(groupId:String){
        _uiState.update { GroupHabitUiState.Loading }
        try {
            viewModelScope.launch {
                val groupHabit = getGroupHabitUseCase(groupId)
                _uiState.update { GroupHabitUiState.GroupHabit(groupHabitMapper.fromGroupHabitsWithHabit(groupHabit)) }

            }
        }catch (e:Exception){
            Log.e("TAG", "getGroupHabit: ${e.printStackTrace()}", )
            _uiState.update { GroupHabitUiState.Error(e.message+"") }
        }

    }

    fun updateHabitEntries(
        habitServerId: String,
        habitId: String,
        entries: HashMap<LocalDate, EntryView>
    ){
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                var habitEntries=0
                try {

                    val h = entries.mapValues { entryMapper.mapToEntry(it.value) }
                    Log.e("TAG", "updateHabitEntries: list ${Gson().toJson(entries)}", )
                    updateHabitEntriesUseCase(
                        habitServerId = habitServerId,
                        habitId = habitId,
                        entries = h.toMutableMap() as HashMap<LocalDate, Entry>
                    )
                    _uiState.update { GroupHabitUiState.Nothing }
                }catch (e:Exception){
                    Log.e("TAG", "updateHabitEntries: due to update entries ${e.printStackTrace()}", )
                }

            }catch (e:Exception){
                Log.e("TAG", "updateHabitEntries: due to get entries $e", )
                _uiState.update { GroupHabitUiState.Error(e.message?:"") }
            }

        }
    }


}