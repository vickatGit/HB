package com.example.habit.ui.mapper.SocialMapper

import com.example.habit.domain.models.Follow.Follow
import com.example.habit.ui.mapper.SocialMapper.UserMapper.toUserView
import com.example.habit.ui.model.Follow.FollowView

object FollowMapper {

    fun Follow.toFollowView(): FollowView {
        return FollowView(
            this.users?.map {
                it.toUserView()
            }
        )
    }
}