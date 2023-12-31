package com.habitude.habit.data.Mapper.SocialMapper

import com.habitude.habit.data.Mapper.SocialMapper.UserMapper.toUserModel
import com.habitude.habit.data.network.model.UsersModel.UserModel
import com.habitude.habit.domain.models.User.User

object UserMapper {
    fun UserModel.toUser():User{
        return User(
            id = this.id,
            email = this.email,
            username = this.username,
            userBio = this.userBio,
            followers = this.followers,
            followings = this.followings,
            avatarUrl = this.avatarUrl
        )
    }
    fun User.toUserModel():UserModel{
        return UserModel(
            id = this.id,
            email = this.email,
            username = this.username,
            userBio = this.userBio,
            followers = this.followers,
            followings = this.followings
        )
    }
}