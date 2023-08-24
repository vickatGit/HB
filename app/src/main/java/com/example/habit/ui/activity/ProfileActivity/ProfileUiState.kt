package com.example.habit.ui.activity.ProfileActivity

import com.example.habit.ui.activity.UserActivity.UserActivityUiState
import com.example.habit.ui.model.User.UserView

sealed class ProfileUiState{
    object Loading : ProfileUiState()
    object Nothing : ProfileUiState()
    data class Error(val error:String) : ProfileUiState()
    data class Success(val msg:String) : ProfileUiState()
    data class Profile(val user: UserView?):ProfileUiState()
}
