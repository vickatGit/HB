package com.habitude.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.domain.UseCases.HabitUseCase.DeleteGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetGroupHabitsUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.RemoveMembersFromHabitGroupUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.UpdateHabitEntriesUseCase
import com.habitude.habit.domain.models.Entry
import com.habitude.habit.ui.fragment.GroupHabit.GroupHabitUiState
import com.habitude.habit.ui.mapper.HabitMapper.EntryMapper
import com.habitude.habit.ui.model.EntryView
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
    private val deleteGroupHabitUseCase: DeleteGroupHabitUseCase,
    private val removeMembersFromHabitGroupUseCase: RemoveMembersFromHabitGroupUseCase,
    private val entryMapper: EntryMapper,
    private val groupHabitMapper: com.habitude.habit.ui.mapper.GroupHabitMapper.GroupHabitMapper
) : ViewModel() {

    var groupId:String? = null
    private var _uiState = MutableStateFlow<GroupHabitUiState>(GroupHabitUiState.Error(""))
    val uiState = _uiState.asStateFlow()

    private var _currentViewingMonth: YearMonth? = null
    fun getCurrentViewingMonth() = _currentViewingMonth
    fun setCurrentViewingMonth(currentViewingMonth: YearMonth) {
        this._currentViewingMonth = currentViewingMonth
    }

    private var userPosition = 0
    fun getHabitStatePos(): Int = userPosition
    fun setHabitStatePos(pos: Int) {
        userPosition = pos
    }


    fun getGroupHabits() {
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                getGroupHabitsUseCase(this).collect { grpHabits ->
                    _uiState.update { GroupHabitUiState.Habits(grpHabits) }
                }
            } catch (e: Exception) {
                Log.e("TAG", "getGroupHabits: error ${e.printStackTrace()}")
                _uiState.update { GroupHabitUiState.Error(e.message + "") }
            }

        }
    }

    fun getGroupHabit(groupId: String) {
        _uiState.update { GroupHabitUiState.Loading }
        try {
            viewModelScope.launch {
                val groupHabit = getGroupHabitUseCase(groupId)
                groupHabit?.let {
                    _uiState.update {
                        GroupHabitUiState.GroupHabit(groupHabitMapper.fromGroupHabitsWithHabit(groupHabit))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("add", "getGroupHabit: ${e.printStackTrace()}")
            _uiState.update { GroupHabitUiState.Error(e.message + "") }
        }

    }

    fun updateHabitEntries(
        habitServerId: String,
        habitId: String,
        entries: HashMap<LocalDate, EntryView>
    ) {
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                val mappedEntries = entries.mapValues { entryMapper.mapToEntry(it.value) }
                Log.e("TAG", "updateHabitEntries: list ${Gson().toJson(entries)}")
                updateHabitEntriesUseCase(
                    habitServerId = habitServerId,
                    habitId = habitId,
                    entries = mappedEntries.toMutableMap() as HashMap<LocalDate, Entry>
                )
                _uiState.update { GroupHabitUiState.Nothing }
                groupId?.let { getGroupHabit(it) }
            } catch (e: Exception) {
                Log.e("TAG", "updateHabitEntries: due to get entries $e")
                _uiState.update { GroupHabitUiState.Error(e.message ?: "") }
            }
        }
    }

    fun deleteGroupHabit(groupHabitId: String, groupHabitServerId: String?) {
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                deleteGroupHabitUseCase(groupHabitServerId, groupHabitId)
                _uiState.update { GroupHabitUiState.Success("Habit Group Deleted Successfully") }
            } catch (e: Exception) {
                Log.e("TAG", "deleteGroupHabit: ${e.printStackTrace()}")
                _uiState.update { GroupHabitUiState.Error(e.message + "") }
            }
        }
    }

    fun removeMembersFromGroupHabit(
        groupHabitServerId: String?,
        groupHabitId: String,
        userIds: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { GroupHabitUiState.Loading }
            try {
                removeMembersFromHabitGroupUseCase.invoke(groupHabitId, userIds, groupHabitServerId)
                _uiState.update { GroupHabitUiState.Success("Exited From Group Successfully") }
            } catch (e: Exception) {
                Log.e("TAG", "removeMembersFromGroupHabit: ${e.printStackTrace()}")
                _uiState.update { GroupHabitUiState.Error(e.message + "") }
            }
        }
    }


}