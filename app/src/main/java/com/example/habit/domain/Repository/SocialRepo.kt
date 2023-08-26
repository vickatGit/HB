package com.example.habit.domain.Repository

import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow

interface SocialRepo {
    fun getUsersByUsername(username:String): Flow<List<User>>

    fun isUserFollowing(friendId:String):Flow<Boolean>

    suspend fun followUser(friendId:String)
    suspend fun unfollowUser(friendId:String)
    fun getUserProfile(userId: String): Flow<User?>
    suspend fun updateUserProfile(user: User): Boolean
    suspend fun getFollowers(): Flow<Follow?>
    suspend fun getFollowings(): Flow<Follow?>
    suspend fun getMembers(): Flow<Follow?>

}