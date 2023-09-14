package com.example.habit.domain.Repository

import com.example.habit.data.network.model.HabitRequestModel.HabitRequestModel
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User
import com.example.habit.domain.models.notification.HabitRequest
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface SocialRepo {
    fun getUsersByUsername(username:String): Flow<List<User>>

    fun isUserFollowing(friendId:String):Flow<Boolean>

    suspend fun followUser(friendId:String): Flow<Any>
    suspend fun unfollowUser(friendId:String): Flow<Any>
    fun getUserProfile(userId: String): Flow<User?>
    suspend fun updateUserProfile(user: User): Flow<Boolean>
    suspend fun getFollowers(): Flow<Follow?>
    suspend fun getFollowings(): Flow<Follow?>
    suspend fun getMembers(): Flow<Follow?>

    suspend fun getHomeData(): HomeData?
    suspend fun HomeDataCreater(asJsonObject: JsonObject?): List<HomeElements>

    suspend fun getHomeElements(asJsonArray: JsonArray?): List<HomeElements>

    suspend fun getHabitRequests(): Flow<List<HabitRequest>?>
}