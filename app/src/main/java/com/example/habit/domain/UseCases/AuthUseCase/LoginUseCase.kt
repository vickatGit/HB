package com.example.habit.domain.UseCases.AuthUseCase

import com.example.habit.domain.Repository.AuthRepo
import com.example.habit.domain.models.Login.Login
import com.example.habit.domain.models.Login.LoginResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepo: AuthRepo){
    suspend operator fun invoke(login: Login): Flow<LoginResponse> {
        return authRepo.login(login)
    }
}