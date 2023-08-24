package com.example.habit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.SocialUseCase.GetProfileUseCase
import com.example.habit.domain.UseCases.SocialUseCase.UpdateProfileUseCase
import com.example.habit.ui.activity.ProfileActivity.ProfileUiState

import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUser
import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.example.habit.ui.model.User.UserView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
): ViewModel() {
    var user: UserView? = null
    private var _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getProfile(userId: String) {
        try {
            _uiState.update { ProfileUiState.Loading }
            viewModelScope.launch {
                getProfileUseCase(userId).collect{ user ->
                    _uiState.update { ProfileUiState.Profile(user?.toUserView()) }
                }            }
        }catch (e:Exception){
            _uiState.update { ProfileUiState.Error("${e.message}") }
        }
    }
    fun updateProfile() {
        user?.let {
            try {
                _uiState.update { ProfileUiState.Loading }
                viewModelScope.launch {
                    Log.e("TAG", "updateProfile: $it", )
                    if (updateProfileUseCase(it.toUser()))
                        _uiState.update { ProfileUiState.Success("User Updated") }
                }
            } catch (e: Exception) {
                _uiState.update { ProfileUiState.Error("${e.message}") }
            }
        }
    }
}