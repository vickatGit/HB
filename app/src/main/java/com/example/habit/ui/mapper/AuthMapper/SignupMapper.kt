package com.example.habit.ui.mapper.AuthMapper

import com.example.habit.data.network.model.SignupModel.SignupResponseView
import com.example.habit.data.network.model.SignupModel.SignupView
import com.example.habit.domain.models.Signup.Signup
import com.example.habit.domain.models.Signup.SignupResponse

class SignupMapper : SignupMapperI<Signup, SignupView, SignupResponseView, SignupResponse> {
    override fun fromSignup(type: SignupView): Signup {
        return Signup(type.email,type.password,type.username)
    }

    override fun toSignupResponse(type: SignupResponse): SignupResponseView {
        return SignupResponseView(type.message,type.success)
    }


}