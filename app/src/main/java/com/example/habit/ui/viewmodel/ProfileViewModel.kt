package com.example.habit.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.R
import com.example.habit.domain.UseCases.SocialUseCase.GetProfileUseCase
import com.example.habit.domain.UseCases.SocialUseCase.UpdateProfileUseCase
import com.example.habit.ui.activity.AddMembersActivity.AddMemberUiState
import com.example.habit.ui.activity.ProfileActivity.ProfileUiState

import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUser
import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.example.habit.ui.model.User.UserView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val context:Application
): ViewModel() {
    var user: UserView? = null
    private var _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getProfile(userId: String) {
        try {
            _uiState.update { ProfileUiState.Loading }
            viewModelScope.launch {
                getProfileUseCase(userId).catch {
                    handleExceptions(java.lang.Exception(it))
                }.collect{ user ->
                    _uiState.update { ProfileUiState.Profile(user?.toUserView()) }
                }            }
        }catch (e:Exception){
            handleExceptions(e)
        }
    }
    fun updateProfile() {
        user?.let {
            try {
                _uiState.update { ProfileUiState.Loading }
                viewModelScope.launch {
                    Log.e("TAG", "updateProfile: $it", )
                    updateProfileUseCase(it.toUser()).catch {
                        handleExceptions(java.lang.Exception(it))
                    }.collect {
                        if(it)
                            _uiState.update { ProfileUiState.Success("Updated Successfully") }
                        else
                            _uiState.update { ProfileUiState.Error("Update failed") }
                    }
                }
            } catch (e: Exception) {
                handleExceptions(e)
            }
        }
    }

    private fun handleExceptions(e:Exception){
        when(e.cause){
            is ConnectException -> {
                _uiState.update { ProfileUiState.Error(context.getString(R.string.unknown_host_error_msg)) }
            }
            is SocketTimeoutException -> {
                _uiState.update { ProfileUiState.Error(context.getString(R.string.socket_timeout_exception)) }
            }
            else -> {
                _uiState.update { ProfileUiState.Error("${e.message}") }
            }
        }
    }
}