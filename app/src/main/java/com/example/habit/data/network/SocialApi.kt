package com.example.habit.data.network

import com.example.habit.data.network.model.FollowModel.FollowerModel
import com.example.habit.data.network.model.FollowModel.FollowingModel
import com.example.habit.data.network.model.IsUserFollowingModel.IsUserFollowingResponseModel
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.example.habit.data.network.model.UsersModel.ProfileModel
import com.example.habit.data.network.model.UsersModel.UserModel
import com.example.habit.data.network.model.UsersModel.UsersModel
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SocialApi {

    @GET("/social/get_users_by_username/{query}")
    suspend fun getUsersByUsername(@Path("query") query:String):Response<UsersModel>

    @GET("/social/is_user_following/{friendId}")
    suspend fun isUserFollowing(@Path("friendId") friendId:String):Response<IsUserFollowingResponseModel>

    @GET("/social/follow/{friendId}")
    suspend fun followUser(@Path("friendId") friendId:String):Response<Any>

    @DELETE("/social/unfollow/{friendId}")
    suspend fun unfollowUser(@Path("friendId") friendId:String):Response<Any>

    @GET("/social/get_profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String):Response<ProfileModel>

    @PATCH("/social/update_profile")
    suspend fun updateProfile(@Body user:UserModel):Response<Any>

    @GET("/social/followers")
    suspend fun getFollowers():Response<FollowerModel>

    @GET("/social/followings")
    suspend fun getFollowings():Response<FollowingModel>

    @GET("/social/get_members")
    suspend fun getMembers():Response<FollowingModel>

    @GET("/social/user")
    suspend fun getUserData():Response<JsonElement>


}