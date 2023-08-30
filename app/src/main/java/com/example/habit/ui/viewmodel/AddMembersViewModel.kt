package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.HabitUseCase.AddMembersToGroupHabitUseCase
import com.example.habit.domain.UseCases.HabitUseCase.GetGroupHabitFromRemoteUseCase
import com.example.habit.domain.UseCases.SocialUseCase.GetMembersUseCase
import com.example.habit.domain.models.GroupHabit
import com.example.habit.domain.models.User.User
import com.example.habit.ui.activity.AddMembersActivity.AddMemberUiState
import com.example.habit.ui.mapper.GroupHabitMapper.GroupHabitMapper
import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.example.habit.ui.model.GroupHabitView
import com.example.habit.ui.model.MemberView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMembersViewModel @Inject constructor(
    private val getMembersUseCase: GetMembersUseCase,
    private val addMemberToGroupHabitUseCase : AddMembersToGroupHabitUseCase,
    private val getGroupHabitFromRemoteUseCase : GetGroupHabitFromRemoteUseCase,
    private val groupHabitMapper: GroupHabitMapper
) : ViewModel() {
    private var _uiState=MutableStateFlow<AddMemberUiState>(AddMemberUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    var groupHabit:GroupHabitView?=null
    var selectedMembers = mutableMapOf<String,String>()

    fun getMembers(){
        try {
            _uiState.update { AddMemberUiState.Loading }
            viewModelScope.launch {
                getMembersUseCase().collect{ users ->
                    val users = users?.users?.toMutableList()
                    val memberUserIds = groupHabit?.members?.map { it.userId }
                    users?.removeIf {
                        return@removeIf memberUserIds?.contains(it.id) == true
                    }
                    Log.e("TAG", "getMembers members: $memberUserIds", )
                    Log.e("TAG", "getMembers: users $users", )
                    _uiState.update { AddMemberUiState.Success(users?.map {
                        it.toUserView()
                    }?: emptyList()
                    ) }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getMembers: ${e.printStackTrace()}", )
            _uiState.update { AddMemberUiState.Error("${e.message}") }
        }
    }

    fun addMembersToGroupHabit(){
        try {
            _uiState.update { AddMemberUiState.Loading }
            viewModelScope.launch {
                groupHabit?.let {
                    addMemberToGroupHabitUseCase(groupHabitMapper.toGroupHabit(it), selectedMembers.values.toList())
                        .collectLatest {
                            Log.e("add", "addMembersToGroupHabit: $it", )
                            _uiState.update { AddMemberUiState.Error("Members Added Successfully") }
                        }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getMembers: ${e.printStackTrace()}", )
            _uiState.update { AddMemberUiState.Error("${e.message}") }
        }
    }

}