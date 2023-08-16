package com.example.habit.domain.UseCases.AuthUseCase

import com.example.habit.domain.Repository.AuthRepo
import com.example.habit.domain.models.Login.Login
import com.example.habit.domain.models.Login.LoginResponse
import com.example.habit.domain.models.Signup.Signup
import com.example.habit.domain.models.Signup.SignupResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignupUseCase @Inject constructor(private val authRepo: AuthRepo){
    suspend operator fun invoke(signup: Signup): Flow<SignupResponse> {
        return authRepo.signup(signup)
    }
}