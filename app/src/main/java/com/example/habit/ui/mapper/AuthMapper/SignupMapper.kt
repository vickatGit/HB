package com.example.habit.ui.mapper.AuthMapper

import com.example.habit.data.network.model.SignupModel.SignupModel
import com.example.habit.data.network.model.SignupModel.SignupResponseModel
import com.example.habit.data.network.model.SignupModel.SignupResponseView
import com.example.habit.data.network.model.SignupModel.SignupView
import com.example.habit.domain.models.Login.Login
import com.example.habit.domain.models.Login.LoginResponse
import com.example.habit.domain.models.Login.LoginResponseView
import com.example.habit.domain.models.Login.LoginView
import com.example.habit.domain.models.Signup.Signup
import com.example.habit.domain.models.Signup.SignupResponse

class SignupMapper : SignupMapperI<Signup, SignupView, SignupResponseView, SignupResponse> {
    override fun fromSignup(type: SignupView): Signup {
        return Signup(type.email,type.password)
    }

    override fun toSignupResponse(type: SignupResponse): SignupResponseView {
        return SignupResponseView(type.message,type.success)
    }


}