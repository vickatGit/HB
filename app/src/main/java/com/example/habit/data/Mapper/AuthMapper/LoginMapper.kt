package com.example.habit.data.Mapper.AuthMapper

import com.example.habit.data.network.model.LoginModel.LoginModel
import com.example.habit.data.network.model.LoginModel.LoginResponseModel
import com.example.habit.data.network.model.SignupModel.SignupModel
import com.example.habit.data.network.model.SignupModel.SignupResponseModel
import com.example.habit.domain.models.Login.Login
import com.example.habit.domain.models.Login.LoginResponse
import com.example.habit.domain.models.Signup.Signup
import com.example.habit.domain.models.Signup.SignupResponse

class LoginMapper : LoginMapperI<Login, LoginModel, LoginResponseModel, LoginResponse> {
    override fun fromLogin(type: Login): LoginModel {
        return LoginModel(
            email = type.email,
            password = type.password,
            fcmToken = type.fcmToken
        )
    }

    override fun toLoginResponse(type: LoginResponseModel): LoginResponse {
        return LoginResponse(
            message = type.message,
            success = type.success
        )
    }
}