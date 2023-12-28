package com.habitude.habit.di

import com.habitude.habit.data.Mapper.AuthMapper.LoginMapper
import com.habitude.habit.data.Mapper.AuthMapper.SignupMapper
import com.habitude.habit.data.Repository.AuthRepoImpl
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.data.network.AuthApi
import com.habitude.habit.domain.Repository.AuthRepo
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
    fun provideAuthApiClient(@HabitModule.MainRetrofit retrofit: Retrofit): AuthApi {
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
    fun uiAuthLoginMapper(): com.habitude.habit.ui.mapper.AuthMapper.LoginMapper {
        return com.habitude.habit.ui.mapper.AuthMapper.LoginMapper()
    }
    @Provides
    fun uiAuthSignupMapper(): com.habitude.habit.ui.mapper.AuthMapper.SignupMapper {
        return com.habitude.habit.ui.mapper.AuthMapper.SignupMapper()
    }
}