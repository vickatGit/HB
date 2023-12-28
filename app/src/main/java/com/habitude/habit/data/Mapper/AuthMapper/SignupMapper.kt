package com.habitude.habit.data.Mapper.AuthMapper

import com.habitude.habit.data.network.model.SignupModel.SignupModel
import com.habitude.habit.data.network.model.SignupModel.SignupResponseModel
import com.habitude.habit.domain.models.Signup.Signup
import com.habitude.habit.domain.models.Signup.SignupResponse

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