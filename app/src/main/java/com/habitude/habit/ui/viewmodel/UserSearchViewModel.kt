package com.habitude.habit.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.habit.R
import com.habitude.habit.domain.UseCases.SocialUseCase.GetFollowersUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.GetFollowingsUseCase
import com.habitude.habit.domain.UseCases.SocialUseCase.GetUsersByUserNameUseCase
import com.habitude.habit.ui.activity.ProfileActivity.ProfileUiState
import com.habitude.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.habitude.habit.ui.mapper.SocialMapper.UserMapper.toUserView
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
class UserSearchViewModel  @Inject constructor(
    private val getUsersByUserNameUseCase: GetUsersByUserNameUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingsUseCase: GetFollowingsUseCase,
    private val context: Application
) : ViewModel() {
    private var _uiState =  MutableStateFlow<UserSearchUiState>(UserSearchUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getUsersByUserName(query:String){

        try {
            viewModelScope.launch {
                _uiState.update { UserSearchUiState.Loading }
                getUsersByUserNameUseCase(query).catch {
                    handleExceptions(Exception(it))
                }.collect{users ->
                    _uiState.update { UserSearchUiState.Success(users.map {
                        it.toUserView()
                    }) }
                }
            }
        }catch (e:Exception){
            handleExceptions(e)
        }
    }
    fun getFollowers(){
        try {
            viewModelScope.launch {
            _uiState.update { UserSearchUiState.Loading }
                getFollowersUseCase().collect{followers ->
                    Log.e("TAG", "getFollowers: $followers" )
                    _uiState.update { UserSearchUiState.Success(
                        followers?.users?.map { it.toUserView() }?: emptyList()
                    ) }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getFollowers: ${e.message}", )
            handleExceptions(e)
        }
    }
    fun getFollowings(){
        try {
            viewModelScope.launch {
                _uiState.update { UserSearchUiState.Loading }
                getFollowingsUseCase().collect{followers ->
                    _uiState.update { UserSearchUiState.Success(
                        followers?.users?.map { it.toUserView() }?: emptyList()
                    ) }
                }
            }
        }catch (e:Exception){
            Log.e("TAG", "getFollowers: ${e.message}", )
            handleExceptions(e)
        }
    }
    private fun handleExceptions(e:Exception){
        Log.e("TAG", "handleExceptions:e ${e.cause} ", )
        Log.e("TAG", "handleExceptions: ${e.printStackTrace()} ", )
        when(e.cause){
            is ConnectException -> {
                _uiState.update { UserSearchUiState.Error(context.getString(R.string.unknown_host_error_msg)) }
            }
            is SocketTimeoutException -> {
                _uiState.update { UserSearchUiState.Error(context.getString(R.string.socket_timeout_exception)) }
            }
            else -> {
                _uiState.update { UserSearchUiState.Error("${e.message}") }
            }
        }
    }
}