package com.example.habit.ui.activity.UserSearchActivity

import com.example.habit.ui.model.User.UserView

sealed class UserSearchUiState {
    object Loading : UserSearchUiState()
    object Nothing : UserSearchUiState()
    data class Success(val users: List<UserView>):UserSearchUiState()
    data class Error(val error:String):UserSearchUiState()
}
