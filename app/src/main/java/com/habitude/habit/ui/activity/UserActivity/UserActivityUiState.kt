package com.habitude.habit.ui.activity.UserActivity

import com.habitude.habit.ui.model.User.UserView

sealed class UserActivityUiState{
    object Loading : UserActivityUiState()
    object Nothing : UserActivityUiState()
    data class Error(val error:String) : UserActivityUiState()
    data class Success(val msg:String) : UserActivityUiState()
    data class UserFollowStatus(val isFollows:Boolean):UserActivityUiState()
    data class Profile(val user: UserView?):UserActivityUiState()
}
