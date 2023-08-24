package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.SocialUseCase.GetUsersByUserNameUseCase
import com.example.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel  @Inject constructor(
    private val getUsersByUserNameUseCase: GetUsersByUserNameUseCase
) : ViewModel() {
    private var _uiState =  MutableStateFlow<UserSearchUiState>(UserSearchUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getUsersByUserName(query:String){

        try {
            viewModelScope.launch {
                _uiState.update { UserSearchUiState.Loading }
                getUsersByUserNameUseCase(query).collect{users ->
                    _uiState.update { UserSearchUiState.Success(users.map {
                        it.toUserView()
                    }) }
                }
            }
        }catch (e:Exception){
            _uiState.update { UserSearchUiState.Error(e.message+"") }
        }
    }
}