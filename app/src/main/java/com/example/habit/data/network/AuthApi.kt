package com.example.habit.data.network

import com.example.habit.data.network.model.LoginModel.LoginModel
import com.example.habit.data.network.model.LoginModel.LoginResponseModel
import com.example.habit.data.network.model.SignupModel.SignupModel
import com.example.habit.data.network.model.SignupModel.SignupResponseModel
import com.example.habit.domain.models.Signup.SignupResponse
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