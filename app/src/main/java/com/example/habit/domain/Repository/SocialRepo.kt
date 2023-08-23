package com.example.habit.domain.Repository

import com.example.habit.data.network.model.UsersModel.User
import com.example.habit.data.network.model.UsersModel.UsersModel
import kotlinx.coroutines.flow.Flow

interface SocialRepo {
    fun getUsersByUsername(username:String): Flow<List<User>>
}