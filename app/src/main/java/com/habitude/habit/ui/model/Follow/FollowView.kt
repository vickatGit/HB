package com.habitude.habit.ui.model.Follow

import com.habitude.habit.ui.model.User.UserView

data class FollowView(
    val users: List<UserView>? = null
)
