package com.example.habit.data.Repository

import android.net.Uri
import android.util.Log
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUserModel
import com.example.habit.data.network.SocialApi
import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SocialRepoImpl(
    private val socialApi: SocialApi
) : SocialRepo {


    override fun getUserProfile(userId: String):Flow<User?>{
        return flow {
            val res = socialApi.getProfile(userId)
            if(res.isSuccessful) {
                Log.e("TAG", "getUserProfile: ${res.body()}", )
                emit(res.body()?.data?.toUser())
            }else{
                emit(null)
            }
        }
    }
    override suspend fun updateUserProfile(user: User):Boolean{
            val res = socialApi.updateProfile(user.toUserModel())
            return res.isSuccessful

    }
    override fun getUsersByUsername(username: String): Flow<List<User>> {
        val query = Uri.encode(username)
        return flow {
            val response = socialApi.getUsersByUsername(query)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(it.data.map { it.toUser() })
                }
            }else{
                Log.e("TAG", "getUsersByUsername: ${response.errorBody()?.string()}", )
            }
        }
    }

    override fun isUserFollowing(friendId: String): Flow<Boolean> {
        return flow {
            val response = socialApi.isUserFollowing(friendId)
            if(response.isSuccessful){
                emit(response.body()?.isFollowing?:false)
            }else{
                Log.e("TAG", "isUserFollowing: ${response.errorBody()?.string()}", )
                emit(false)
            }
        }
    }

    override suspend fun followUser(friendId: String) {
        socialApi.followUser(friendId)
    }

    override suspend fun unfollowUser(friendId: String) {
        socialApi.unfollowUser(friendId)
    }


}