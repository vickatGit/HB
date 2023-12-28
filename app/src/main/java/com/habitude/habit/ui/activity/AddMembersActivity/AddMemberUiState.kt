package com.habitude.habit.ui.activity.AddMembersActivity

import com.habitude.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.habitude.habit.ui.model.User.UserView

sealed class AddMemberUiState{
    object Loading : AddMemberUiState()
    object Nothing : AddMemberUiState()
    data class Success(val users: List<UserView>): AddMemberUiState()
    data class Error(val error:String): AddMemberUiState()
}
