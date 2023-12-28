package com.habitude.habit.domain.Repository

import com.habitude.habit.domain.models.Login.Login
import com.habitude.habit.domain.models.Login.LoginResponse
import com.habitude.habit.domain.models.Signup.Signup
import com.habitude.habit.domain.models.Signup.SignupResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepo {
    suspend fun login(login:Login) : Flow<LoginResponse>
    suspend fun signup(signup: Signup) : Flow<SignupResponse>
}