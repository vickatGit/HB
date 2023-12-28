package com.habitude.habit.domain.UseCases.AuthUseCase

import com.habitude.habit.domain.Repository.AuthRepo
import com.habitude.habit.domain.models.Login.Login
import com.habitude.habit.domain.models.Login.LoginResponse
import com.habitude.habit.domain.models.Signup.Signup
import com.habitude.habit.domain.models.Signup.SignupResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignupUseCase @Inject constructor(private val authRepo: AuthRepo){
    suspend operator fun invoke(signup: Signup): Flow<SignupResponse> {
        return authRepo.signup(signup)
    }
}