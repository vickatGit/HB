package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.SocialUseCase.FollowUseCase
import com.example.habit.domain.UseCases.SocialUseCase.GetProfileUseCase
import com.example.habit.domain.UseCases.SocialUseCase.IsUserFollowingUseCase
import com.example.habit.domain.UseCases.SocialUseCase.UnfollowUseCase
import com.example.habit.domain.UseCases.SocialUseCase.UpdateProfileUseCase
import com.example.habit.ui.activity.UserActivity.UserActivityUiState
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
class UserViewModel @Inject constructor(
    private val isUserFollowingUseCase: IsUserFollowingUseCase,
    private val followUseCase: FollowUseCase,
    private val unfollowUseCase: UnfollowUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {
    var user: UserView? = null
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

    fun followUser(friendId: String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                followUseCase(friendId)
                _uiState.update { UserActivityUiState.Success("Followed") }
            }
        }catch (e:Exception){
            _uiState.update { UserActivityUiState.Error("${e.message}") }
        }
    }

    fun unfollowUser(friendId: String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                unfollowUseCase(friendId)
                _uiState.update { UserActivityUiState.Success("Unfollowed") }
            }
        }catch (e:Exception){
            _uiState.update { UserActivityUiState.Error("${e.message}") }
        }
    }
    fun getProfile(userId:String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                getProfileUseCase(userId).collect{user ->
                    _uiState.update { UserActivityUiState.Profile(user?.toUserView()) }
                }            }
        }catch (e:Exception){
            _uiState.update { UserActivityUiState.Error("${e.message}") }
        }
    }
}