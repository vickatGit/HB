package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.SocialUseCase.IsUserFollowingUseCase
import com.example.habit.ui.activity.UserActivity.UserActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val isUserFollowingUseCase: IsUserFollowingUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow<UserActivityUiState>(UserActivityUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun isUserFollowing(friendId:String){
        try {
            _uiState.update { UserActivityUiState.Loading }
           viewModelScope.launch {
               isUserFollowingUseCase(friendId).collect{isFollowing ->
                   _uiState.update { UserActivityUiState.UserFollowStatus(isFollowing) }
               }
           }
        }catch (e:Exception){
            _uiState.update { UserActivityUiState.Error("${e.message}") }
        }
    }
}