package com.example.habit.data.Mapper.SocialMapper

import com.example.habit.data.network.model.UsersModel.UserModel
import com.example.habit.domain.models.User.User

object UserMapper {
    fun UserModel.toUser():User{
        return User(
            id = this.id,
            email = this.email,
            username = this.username
        )
    }
}