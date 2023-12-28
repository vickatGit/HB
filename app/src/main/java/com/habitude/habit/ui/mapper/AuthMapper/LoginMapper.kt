package com.habitude.habit.ui.mapper.AuthMapper

import com.habitude.habit.domain.models.Login.Login
import com.habitude.habit.domain.models.Login.LoginResponse
import com.habitude.habit.domain.models.Login.LoginResponseView
import com.habitude.habit.domain.models.Login.LoginView


class LoginMapper : LoginMapperI<Login,LoginView,LoginResponseView,LoginResponse> {
    override fun fromLogin(type: LoginView): Login {
        return Login(type.email,type.password,type.fcmToken)
    }

    override fun toLoginResponse(type: LoginResponse): LoginResponseView {
        return LoginResponseView(type.message,type.success,)
    }

}