package com.example.habit.data.Mapper.AuthMapper

import com.example.habit.data.network.model.SignupModel.SignupModel
import com.example.habit.data.network.model.SignupModel.SignupResponseModel
import com.example.habit.domain.models.Signup.Signup
import com.example.habit.domain.models.Signup.SignupResponse

class SignupMapper : SignupMapperI<Signup,SignupModel,SignupResponseModel,SignupResponse> {
    override fun fromSignup(type: Signup): SignupModel {
        return SignupModel(
            email = type.email,
            password = type.password,
            username = type.username
        )
    }

    override fun toSignupResponse(type: SignupResponseModel): SignupResponse {
        return SignupResponse(message = type.message, success = type.success)
    }

}