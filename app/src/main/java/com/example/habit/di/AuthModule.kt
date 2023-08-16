package com.example.habit.di

import com.example.habit.data.Mapper.AuthMapper.LoginMapper
import com.example.habit.data.Mapper.AuthMapper.SignupMapper
import com.example.habit.data.Repository.AuthRepoImpl
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.AuthApi
import com.example.habit.domain.Repository.AuthRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun provideAuthApiClient(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepo(authApi: AuthApi,authPref: AuthPref):AuthRepo{
        return AuthRepoImpl(
            SignupMapper(),
            LoginMapper(),
            authApi,
            authPref
        )
    }

    @Provides
    fun uiAuthLoginMapper(): com.example.habit.ui.mapper.AuthMapper.LoginMapper {
        return com.example.habit.ui.mapper.AuthMapper.LoginMapper()
    }
    @Provides
    fun uiAuthSignupMapper(): com.example.habit.ui.mapper.AuthMapper.SignupMapper {
        return com.example.habit.ui.mapper.AuthMapper.SignupMapper()
    }
}