package com.habitude.habit.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.R
import com.habitude.habit.domain.UseCases.SocialUseCase.FollowUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.GetProfileUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.IsUserFollowingUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.UnfollowUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.UpdateProfileUseCase
import com.habitude.habit.ui.activity.UserActivity.UserActivityUiState
import com.habitude.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.habitude.habit.ui.mapper.SocialMapper.UserMapper.toUser
import com.habitude.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.habitude.habit.ui.model.User.UserView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val isUserFollowingUseCase: IsUserFollowingUseCase,
    private val followUseCase: FollowUseCase,
    private val unfollowUseCase: UnfollowUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val context:Application
) : ViewModel() {
    var followStatus: Boolean = false
    var user: UserView? = null
    private var _uiState = MutableStateFlow<UserActivityUiState>(UserActivityUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun isUserFollowing(friendId:String){
        try {
            _uiState.update { UserActivityUiState.Loading }
           viewModelScope.launch {
               isUserFollowingUseCase(friendId)
                   .catch {
                       handleExceptions(Exception(it))
                   }
                   .collect{isFollowing ->
                   _uiState.update { UserActivityUiState.UserFollowStatus(isFollowing) }
               }
           }
        }catch (e:Exception){
            handleExceptions(e)
        }
    }

    fun followUser(friendId: String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                followUseCase(friendId).catch {
                    Log.e("TAG", "followUser: $it", )
                    handleExceptions(java.lang.Exception(it))
                }.collect { res ->
                    Log.e("TAG", "followUser: user followed", )
//                    _uiState.update { UserActivityUiState.Success("Followed") }
                    _uiState.update { if(res) UserActivityUiState.Success("Followed") else UserActivityUiState.Success("Unable to follow user") }
                }
            }
        }catch (e:Exception){
            handleExceptions(e)
            Log.e("TAG", "followUser: $e", )
        }
    }

    fun unfollowUser(friendId: String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                unfollowUseCase(friendId).catch {
                    handleExceptions(java.lang.Exception(it))
                }.collect { res ->
                    _uiState.update { if(res) UserActivityUiState.Success("Unfollowed") else UserActivityUiState.Success("Unable to unfollow user") }
                }
            }
        }catch (e:Exception){
            handleExceptions(e)
        }
    }
    fun getProfile(userId:String){
        try {
            _uiState.update { UserActivityUiState.Loading }
            viewModelScope.launch {
                getProfileUseCase(userId).catch {
                    handleExceptions(java.lang.Exception(it))
                }.collect{user ->
                    _uiState.update { UserActivityUiState.Profile(user?.toUserView()) }
                }            }
        }catch (e:Exception){
            handleExceptions(e)
        }
    }
    private fun handleExceptions(e:Exception){
//        Toast.makeText(context,"called ${e.cause}",Toast.LENGTH_SHORT).show()
        when(e.cause){
            is ConnectException -> {
                _uiState.update { UserActivityUiState.Error(context.getString(R.string.unknown_host_error_msg)) }
            }
            is SocketTimeoutException -> {
                _uiState.update { UserActivityUiState.Error(context.getString(R.string.socket_timeout_exception)) }
            }
            else -> {
                _uiState.update { UserActivityUiState.Error("${e.message}") }
            }
        }
    }
}