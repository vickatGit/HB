package com.habitude.habit.data.Mapper.SocialMapper

import com.habitude.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.habitude.habit.data.network.model.FollowModel.FollowerModel
import com.habitude.habit.data.network.model.FollowModel.FollowingModel
import com.habitude.habit.domain.models.Follow.Follow
import com.habitude.habit.domain.models.User.User

object FollowMapper {
    fun FollowerModel.toFollow(): Follow {
        val users  = mutableListOf<User>()
        this.users?.map {
            it?.let {
                users.add(it.toUser())
            }
        }
        return Follow(users)
    }

    fun FollowingModel.toFollow(): Follow {
        val users  = mutableListOf<User>()
        this.users?.map {
            it?.let {
                users.add(it.toUser())
            }
        }
        return Follow(users)
    }
}