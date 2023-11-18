package com.example.habit.data.Repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.habit.data.Mapper.NotificationMapper.NotificationMapper.toHabitRequest
import com.example.habit.data.Mapper.SocialMapper.FollowMapper.toFollow
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUserModel
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.SocialApi
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.data.network.model.UiModels.HomePageModels.Sections
import com.example.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactory
import com.example.habit.di.HabitModule
import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User
import com.example.habit.domain.models.notification.HabitRequest
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.InputStreamReader

class SocialRepoImpl(
    @HabitModule.MainRetrofit private val socialApi: SocialApi,
    @HabitModule.WithoutAuthRetrofit private val noAuthSocialApi: SocialApi,
    private val context:Context,
    private val homeElementsFactory: HomeSectionsFactory,
    private val authPref: AuthPref
) : SocialRepo {

    private var lastFetchTime:Long?=null
    override fun getUserProfile(userId: String):Flow<User?>{
        // if(!Connectivity.isInternetConnected(context)) throw UnknownHostException()
        return flow {
            try {
                val res = socialApi.getProfile(userId)
                if(res.isSuccessful) {
                    Log.e("TAG", "getUserProfile: ${res.body()}", )
                    val user = res.body()?.data
                    user?.avatarUrl = res.body()?.avatarUrl
                    emit(user?.toUser())
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
        var apiShouldBeCalled=authPref.getApiShouldBeCalled()
        var data:List<HomeElements>? = null
        val json = readUiFromFile()
//        Log.e("TAG", "getHomeData json : ${json!!.length}", )

        if(json==null || json=="null") apiShouldBeCalled=true
//        else if(lastFetchTime!=null) apiShouldBeCalled = (System.currentTimeMillis() - lastFetchTime!!)>=(10 * 60 * 1000)

        if(apiShouldBeCalled) {
            Log.e("TAG", "getHomeData: api called", )
            val response = socialApi.getUserData()
            val json = response.body()
            data = HomeDataCreater(json?.asJsonObject?.getAsJsonObject("data"))
            Log.e("TAG", "getHomeData: ${Gson().toJson(json?.asJsonObject)}")
            writeUiToFile(json?.asJsonObject)
            lastFetchTime=System.currentTimeMillis()
            authPref.putApiShouldBeCalled(false)
        }else{
            json?.let {
                try {
                    val jsonElement = JsonParser.parseString(it)
                    val jsonObj: JsonObject = jsonElement.asJsonObject
                    data = HomeDataCreater(jsonObj?.getAsJsonObject("data"))
                }catch (e:Exception){
                    return@let
                }

            }
        }


        return data?.let { HomeData( Sections(it) ) }?:null
    }

    private fun writeUiToFile(data: JsonObject?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream = context.openFileOutput("server_ui.json", Context.MODE_PRIVATE)
                outputStream.write(Gson().toJson(data).toString().toByteArray())
                outputStream.close()
            } catch (e: Exception) {
                Log.e("TAG", "writeUiToFile: e ", )
                Log.e("TAG", "writeUiToFile: ${e.printStackTrace()}", )
            }
        }

    }
    private fun readUiFromFile(): String? {
        try {
            val inputStream = context.openFileInput("server_ui.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line:String?
            while (reader.readLine().also { line = it }!=null){
                stringBuilder.append(line)
            }
            return stringBuilder.toString()

        }catch (e:Exception){
            Log.e("TAG", "readUiFromFile: ${e.message} ", )
            Log.e("TAG", "readUiFromFile: ${e.printStackTrace()}", )
            return null
        }
    }

    override suspend fun HomeDataCreater(json: JsonObject?): List<HomeElements> {
        return getHomeElements(json?.getAsJsonArray("sections"))
    }

    override suspend fun getHomeElements(sections: JsonArray?): List<HomeElements> {
        return homeElementsFactory.create(sections)
    }

    override suspend fun getHabitRequests(): Flow<List<HabitRequest>?> {
        return flow {
            val response = socialApi.getHabitRequests()
            if(response.isSuccessful)
                emit(response.body()?.habitRequests?.map {
                    it.toHabitRequest()
                })
            else {
                Log.e("TAG", "getHabitRequests: ${response.errorBody()?.string()}", )
                emit(null)
            }
        }
    }

    override fun acceptHabitRequest(habitGroupId:String): Flow<Boolean> {
        return flow {
            val response = socialApi.acceptHabitRequest(habitGroupId)
            if(response.isSuccessful)
                emit(true)
            else
                emit(false)
        }
    }

    override fun rejectHabitRequest(habitGroupId:String): Flow<Boolean> {
        return flow {
            val response = socialApi.rejectHabitRequest(habitGroupId)
            if(response.isSuccessful)
                emit(true)
            else
                emit(false)
        }
    }

    override suspend fun uploadUserAvatar(requestBody: RequestBody): Flow<Boolean> {
        return flow{
            val getPreSignedUrlResponse = socialApi.getAvatarUploadUrl()
            if(getPreSignedUrlResponse.isSuccessful){
                getPreSignedUrlResponse.body()?.let {
                    Log.e("TAG", "uploadUserAvatar: ${it}", )
                    val uploadAvatarResponse = noAuthSocialApi.uploadAvatar(it.url,requestBody)
                    Log.e("TAG", "uploadUserAvatar response : ${uploadAvatarResponse}", )
                    if(uploadAvatarResponse.isSuccessful){
                        authPref.putApiShouldBeCalled(true)
                        emit(true)
                    }
                }
                if(getPreSignedUrlResponse.body()==null)  throw Exception("Failed to Upload Profile Image")

            }else{
                throw Exception("Failed to Upload Profile Image")
            }
        }

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