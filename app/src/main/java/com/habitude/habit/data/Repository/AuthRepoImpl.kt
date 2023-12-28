package com.habitude.habit.data.Repository

import android.util.Log
import com.habitude.habit.data.Mapper.AuthMapper.LoginMapper
import com.habitude.habit.data.Mapper.AuthMapper.SignupMapper
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.data.network.AuthApi
import com.habitude.habit.data.network.model.LoginModel.LoginResponseModel
import com.habitude.habit.data.network.model.SignupModel.SignupResponseModel
import com.habitude.habit.domain.Repository.AuthRepo
import com.habitude.habit.domain.models.Login.Login
import com.habitude.habit.domain.models.Login.LoginResponse
import com.habitude.habit.domain.models.Signup.Signup
import com.habitude.habit.domain.models.Signup.SignupResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepoImpl(
    private val signupMapper: SignupMapper,
    private val loginMapper: LoginMapper,
    private val authApi: AuthApi,
    private val authPref: AuthPref
) : AuthRepo {
    override suspend fun login(login: Login): Flow<LoginResponse> {
        return flow {
            val response = authApi.login(loginMapper.fromLogin(login))
            if(response.isSuccessful){
                response.body()?.let {
                    authPref.setToken(it.token)
                    authPref.setUserId(it.userId)
                    authPref.setUserName(it.userName)
                    emit(loginMapper.toLoginResponse(response.body()!!))
                }

            } else {
                Log.e("TAG", "login: ${response.errorBody()}", )
                val type  = object : TypeToken<LoginResponseModel>(){}.type
                val err:LoginResponseModel = Gson().fromJson(response.errorBody()!!.string(),type)
                emit(LoginResponse(err.error,err.success))
            }
        }

    }

    override suspend fun signup(signup: Signup): Flow<SignupResponse> {
        return flow {
            val response = authApi.signup(signupMapper.fromSignup(signup))
            if(response.isSuccessful){
                emit(signupMapper.toSignupResponse(response.body()!!))
            } else {
                val type  = object : TypeToken<SignupResponseModel>(){}.type
                val err:SignupResponseModel = Gson().fromJson(response.errorBody()!!.string(),type)
//                Log.e("TAG", "login: ${Gson().toJson(response)}", )
                emit(SignupResponse(err.error,err.success))
            }
        }
    }
}