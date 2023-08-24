package com.example.habit.data.network

import com.example.habit.data.network.model.IsUserFollowingModel.IsUserFollowingResponseModel
import com.example.habit.data.network.model.UsersModel.UsersModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface SocialApi {

    @GET("/social/get_users_by_username/{query}")
    suspend fun getUsersByUsername(@Path("query") query:String):Response<UsersModel>

    @GET("/social/is_user_following/{friendId}")
    suspend fun isUserFollowing(@Path("friendId") friendId:String):Response<IsUserFollowingResponseModel>
}