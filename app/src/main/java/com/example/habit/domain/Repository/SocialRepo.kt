package com.example.habit.domain.Repository

import com.example.habit.data.network.model.UsersModel.UserModel
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow

interface SocialRepo {
    fun getUsersByUsername(username:String): Flow<List<User>>

    fun isUserFollowing(friendId:String):Flow<Boolean>
}