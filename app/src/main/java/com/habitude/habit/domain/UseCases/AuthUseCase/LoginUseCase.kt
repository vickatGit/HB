package com.habitude.habit.domain.UseCases.AuthUseCase

import com.habitude.habit.domain.Repository.AuthRepo
import com.habitude.habit.domain.models.Login.Login
import com.habitude.habit.domain.models.Login.LoginResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepo: AuthRepo){
    suspend operator fun invoke(login: Login): Flow<LoginResponse> {
        return authRepo.login(login)
    }
}