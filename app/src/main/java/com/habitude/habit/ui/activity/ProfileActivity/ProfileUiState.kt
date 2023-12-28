package com.habitude.habit.ui.activity.ProfileActivity

import com.habitude.habit.ui.activity.UserActivity.UserActivityUiState
import com.habitude.habit.ui.model.User.UserView

sealed class ProfileUiState{
    object Loading : ProfileUiState()
    object Nothing : ProfileUiState()
    data class Error(val error:String) : ProfileUiState()
    data class Success(val msg:String) : ProfileUiState()
    data class Profile(val user: UserView?):ProfileUiState()
}
