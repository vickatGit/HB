package com.example.habit.data.Mapper.SocialMapper

import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.example.habit.data.network.model.FollowModel.FollowerModel
import com.example.habit.data.network.model.FollowModel.FollowingModel
import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User

object FollowMapper {
    fun FollowerModel.toFollow(): Follow {
        val users  = mutableListOf<User>()
        this.users?.map {
            it?.follows?.let {
                users.add(it.toUser())
            }
        }
        return Follow(users)
    }

    fun FollowingModel.toFollow(): Follow {
        val users  = mutableListOf<User>()
        this.users?.map {
            it?.to?.let {
                users.add(it.toUser())
            }
        }
        return Follow(users)
    }
}