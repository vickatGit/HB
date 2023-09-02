package com.example.habit.data.Repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.habit.data.Mapper.SocialMapper.FollowMapper.toFollow
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUserModel
import com.example.habit.data.network.SocialApi
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.data.network.model.UiModels.HomePageModels.Sections
import com.example.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactory
import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SocialRepoImpl(
    private val socialApi: SocialApi,
    private val context:Context,
    private val homeElementsFactory: HomeSectionsFactory
) : SocialRepo {


    override fun getUserProfile(userId: String):Flow<User?>{
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            try {
                val res = socialApi.getProfile(userId)
                if(res.isSuccessful) {
                    Log.e("TAG", "getUserProfile: ${res.body()}", )
                    emit(res.body()?.data?.toUser())
                }else{
                    emit(null)
                }
            }catch (e:Exception){
                throw e
            }

        }
    }
    override suspend fun updateUserProfile(user: User): Flow<Boolean> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            val res = socialApi.updateProfile(user.toUserModel())
            emit(res.isSuccessful)
        }

    }

    override suspend fun getFollowers(): Flow<Follow?> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            try {
                val response = socialApi.getFollowers()
                Log.e("TAG", "getFollowers: repo ${response.body()}",)
                if (response.isSuccessful) {
                    emit(response.body()?.toFollow())
                } else {
                    emit(null)
                }
            }catch (e:Exception) {
                throw e
            }
        }
    }

    override suspend fun getFollowings(): Flow<Follow?> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            try {
                val response = socialApi.getFollowings()
                if (response.isSuccessful) {
                    emit(response.body()?.toFollow())
                } else {
                    emit(null)
                }
            }catch (e:Exception) {
                throw e
            }
        }
    }

    override suspend fun getMembers(): Flow<Follow?> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            try {
                val response = socialApi.getMembers()
                if(response.isSuccessful){
                    emit(response.body()?.toFollow())
                }else{
                    emit(null)
                }
            }catch (e:Exception){
                Log.e("TAG", "getMembers: ${e.message}", )
                throw e
            }

        }
    }

    override suspend fun getHomeData(): HomeData? {
        val response = socialApi.getUserData()
        val json=response.body()
        val data = HomeDataCreater(json?.asJsonObject?.getAsJsonObject("data"))
        Log.e("TAG", "getHomeData: ${Gson().toJson(data) }")

        return HomeData(Sections(data))
    }

    override suspend fun HomeDataCreater(json: JsonObject?): List<HomeElements> {
        return getHomeElements(json?.getAsJsonArray("sections"))
    }

    override suspend fun getHomeElements(sections: JsonArray?): List<HomeElements> {
        return homeElementsFactory.create(sections)
    }


    override fun getUsersByUsername(username: String): Flow<List<User>> {
//        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
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
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
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

    override suspend fun followUser(friendId: String): Flow<Any> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
            return flow { socialApi.followUser(friendId) }

    }

    override suspend fun unfollowUser(friendId: String): Flow<Any> {
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
            return flow {  socialApi.unfollowUser(friendId) }
    }


}