package com.example.habit.ui.activity.UserActivity

sealed class UserActivityUiState{
    object Loading : UserActivityUiState()
    object Nothing : UserActivityUiState()
    data class Error(val error:String) : UserActivityUiState()
    data class UserFollowStatus(val isFollows:Boolean):UserActivityUiState()
}
