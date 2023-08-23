package com.example.habit.data.Repository

import android.util.Log
import com.example.habit.data.network.SocialApi
import com.example.habit.data.network.model.UsersModel.User
import com.example.habit.data.network.model.UsersModel.UsersModel
import com.example.habit.domain.Repository.SocialRepo
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

class SocialRepoImpl(
    private val socialApi: SocialApi
) : SocialRepo {
    override fun getUsersByUsername(username: String): Flow<List<User>> {
        return flow {
            val response = socialApi.getUsersByUsername(username)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(it.data)
                }
            }else{
                Log.e("TAG", "getUsersByUsername: ${response.errorBody()?.string()}", )
            }
        }
    }


}