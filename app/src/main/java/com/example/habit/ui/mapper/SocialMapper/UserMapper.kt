package com.example.habit.ui.mapper.SocialMapper

import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUserModel
import com.example.habit.domain.models.User.User

import com.example.habit.ui.model.User.UserView

object UserMapper {
    fun User.toUserView():UserView{
        return UserView(
            id = this.id,
            email = this.email,
            username = this.username,
            userBio = this.userBio,
            followers = this.followers,
            followings = this.followings,
            avatarUrl = this.avatarUrl
        )
    }
    fun UserView.toUser():User{
        return User(
            id = this.id,
            email = this.email,
            username = this.username,
            userBio = this.userBio,
            followers = this.followers,
            followings = this.followings,

        )
    }

}