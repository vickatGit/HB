package com.example.habit.ui.activity.UserSearchActivity

import com.example.habit.data.network.model.UsersModel.User

sealed class UserSearchUiState {
    object Loading : UserSearchUiState()
    object Nothing : UserSearchUiState()
    data class Success(val users:List<User>):UserSearchUiState()
    data class Error(val error:String):UserSearchUiState()
}
