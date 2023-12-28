package com.habitude.habit.data.network

import com.habitude.habit.data.network.model.LoginModel.LoginModel
import com.habitude.habit.data.network.model.LoginModel.LoginResponseModel
import com.habitude.habit.data.network.model.SignupModel.SignupModel
import com.habitude.habit.data.network.model.SignupModel.SignupResponseModel
import com.habitude.habit.domain.models.Signup.SignupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body login:LoginModel):Response<LoginResponseModel>

    @POST("auth/signup")
    suspend fun signup(@Body login:SignupModel):Response<SignupResponseModel>
}