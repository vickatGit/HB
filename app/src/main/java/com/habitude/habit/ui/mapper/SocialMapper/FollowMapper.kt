package com.habitude.habit.ui.mapper.SocialMapper

import com.habitude.habit.domain.models.Follow.Follow
import com.habitude.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.habitude.habit.ui.model.Follow.FollowView

object FollowMapper {

    fun Follow.toFollowView(): FollowView {
        return FollowView(
            this.users?.map {
                it.toUserView()
            }
        )
    }
}