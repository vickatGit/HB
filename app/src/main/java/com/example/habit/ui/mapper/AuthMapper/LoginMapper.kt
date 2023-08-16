package com.example.habit.ui.mapper.AuthMapper

import com.example.habit.domain.models.Login.Login
import com.example.habit.domain.models.Login.LoginResponse
import com.example.habit.domain.models.Login.LoginResponseView
import com.example.habit.domain.models.Login.LoginView


class LoginMapper : LoginMapperI<Login,LoginView,LoginResponseView,LoginResponse> {
    override fun fromLogin(type: LoginView): Login {
        return Login(type.email,type.password)
    }

    override fun toLoginResponse(type: LoginResponse): LoginResponseView {
        return LoginResponseView(type.message,type.success,)
    }

}