package com.habitude.habit.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.R
import com.habitude.habit.domain.UseCases.HabitUseCase.AddMembersToGroupHabitUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.GetGroupHabitFromRemoteUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.GetMembersUseCase
import com.habitude.habit.domain.models.GroupHabit
import com.habitude.habit.domain.models.User.User
import com.habitude.habit.ui.activity.AddMembersActivity.AddMemberUiState
import com.habitude.habit.ui.mapper.GroupHabitMapper.GroupHabitMapper
import com.habitude.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.habitude.habit.ui.model.GroupHabitView
import com.habitude.habit.ui.model.MemberView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AddMembersViewModel @Inject constructor(
    private val getMembersUseCase: GetMembersUseCase,
    private val addMemberToGroupHabitUseCase : AddMembersToGroupHabitUseCase,
    private val getGroupHabitFromRemoteUseCase : GetGroupHabitFromRemoteUseCase,
    private val groupHabitMapper: GroupHabitMapper,
    private val context:Application
) : ViewModel() {
    private var _uiState=MutableStateFlow<AddMemberUiState>(AddMemberUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    var groupHabit:GroupHabitView?=null
    var selectedMembers = mutableMapOf<String,String>()

    fun getMembers(){
        try {
            _uiState.update { AddMemberUiState.Loading }
            viewModelScope.launch {
                getMembersUseCase().catch {err ->
                    _uiState.update { AddMemberUiState.Error("${err.message}") }
                }.collect{ users ->
                    val users = users?.users?.toMutableList()
                    val memberUserIds = groupHabit?.members?.map { it.userId }
                    users?.removeIf { return@removeIf memberUserIds?.contains(it.id) == true }
                    _uiState.update { AddMemberUiState.Success(users?.map { it.toUserView() }?: emptyList()) }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getMembers: ui ${e.printStackTrace()}", )
            _uiState.update { AddMemberUiState.Error("${e.message}") }
        }
    }

    fun addMembersToGroupHabit(){
        try {
            _uiState.update { AddMemberUiState.Loading }
            viewModelScope.launch {
                groupHabit?.let {
                    addMemberToGroupHabitUseCase(groupHabitMapper.toGroupHabit(it), selectedMembers.values.toList())
                        .catch {
                            handleExceptions(java.lang.Exception(it))
                        }
                        .collectLatest {
                            Log.e("add", "addMembersToGroupHabit: $it", )
                            _uiState.update { AddMemberUiState.Error("Members Added Successfully") }
                        }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getMembers: ${e.printStackTrace()}", )
            handleExceptions(e)
        }
    }

    private fun handleExceptions(e:Exception){
        when(e.cause){
            is ConnectException -> {
                _uiState.update { AddMemberUiState.Error(context.getString(R.string.unknown_host_error_msg)) }
            }
            is SocketTimeoutException -> {
                _uiState.update { AddMemberUiState.Error(context.getString(R.string.socket_timeout_exception)) }
            }
            else -> {
                _uiState.update { AddMemberUiState.Error("${e.message}") }
            }
        }
    }

}